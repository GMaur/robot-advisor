package com.gmaur.investment.robotadvisor

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.gmaur.investment.robotadvisor.RobotAdvisorAppFeatureComplete.RealPortfolioRebalancer
import com.gmaur.investment.robotadvisor.domain.*
import com.gmaur.investment.robotadvisor.infrastructure.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [RobotAdvisorApp::class, RealPortfolioRebalancer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RobotAdvisorAppFeatureComplete {
    @LocalServerPort
    var port: Int? = null

    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    @Before
    fun setUp() {
        FuelManager.instance.basePath = "http://localhost:" + port!!
    }

    @Test
    fun `balances a portfolio comparing to the ideal distribution`() {
        val assetAllocation = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage(".80")),
                AssetAllocationSingle(ISIN("LU2"), Percentage(".20")))).get()
        val currentPortfolio = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal("8")), false),
                Asset(ISIN("LU2"), Amount(BigDecimal("2")), false),
                Asset(ISIN("LU2"), Amount(BigDecimal("90")), true)
        ))

        val response = balancePortfolio(assetAllocation, currentPortfolio)

        assertThat(response.isRight())
        response.bimap(
                {
                    fail("expected a right")
                },
                { (response, result) ->
                    val get = result.get()
                    assertThat(response.statusCode).isEqualTo(200)
                    when (result) {
                        is Result.Success -> {
                            assertThat(deserialize(result.value)).isEqualTo(
                                    OperationsDTO(listOf(
                                            OperationDTO(type = "Purchase", asset = AssetDTO("LU1"), amount = AmountDTO("72.00", currency = "EUR")),
                                            OperationDTO(type = "Purchase", asset = AssetDTO("LU2"), amount = AmountDTO("18.00", currency = "EUR"))
                                    )))
                        }
                        else -> {
                            fail("expected a Result.success")
                        }
                    }
                })
    }

    private fun deserialize(get: String): OperationsDTO {
        return objectMapper.readValue<OperationsDTO>(get, OperationsDTO::class.java)
    }


    private fun balancePortfolio(idealDistribution: AssetAllocation, currentDistribution: Portfolio): Either<Exception, Pair<Response, Result<String, FuelError>>> {
        val request = RebalanceRequest(ideal = idealDistribution, current = currentDistribution)
        val jsonPayload = serialize(request)
        return balancePortfolio(jsonPayload)
    }

    private fun balancePortfolio(jsonPayload: String): Either<Exception, Pair<Response, Result<String, FuelError>>> {
        val httpPost = "/rebalance/".httpPost().body(jsonPayload, Charsets.UTF_8).header("Content-Type" to "application/json")
        try {
            val (_, response, result) = httpPost.responseString()
            return Either.right(Pair(response, result))
        } catch (e: Exception) {
            e.printStackTrace()
            return Either.left(e)
        }

    }


    private fun serialize(request: RebalanceRequest): String {
        val mapper: ObjectMapper = objectMapper
        return mapper.writeValueAsString(request)
    }

    @Configuration
    class RealPortfolioRebalancer {
        @Bean
        fun portfolioRebalancer(): PortfolioRebalancer {
            return PortfolioRebalancer(FixedStrategy)
        }
    }

}

