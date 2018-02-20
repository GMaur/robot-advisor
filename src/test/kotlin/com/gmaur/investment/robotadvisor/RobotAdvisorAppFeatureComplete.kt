package com.gmaur.investment.robotadvisor

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.gmaur.investment.robotadvisor.RobotAdvisorAppFeatureComplete.RealPortfolioRebalancer
import com.gmaur.investment.robotadvisor.domain.FixedStrategy
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
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

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [RobotAdvisorApp::class, RealPortfolioRebalancer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RobotAdvisorAppFeatureComplete {
    @LocalServerPort
    var port: Int? = null

    private val objectMapper: ObjectMapper = JSONMapper.aNew()

    @Before
    fun setUp() {
        FuelManager.instance.basePath = "http://localhost:" + port!!
    }

    @Test
    fun `balances a portfolio comparing to the ideal distribution`() {
        val assetAllocation = AssetAllocationDTO(listOf(
                AssetAllocationElementDTO(isin = "LU1", percentage = "80%"),
                AssetAllocationElementDTO(isin = "LU2", percentage = "20%")))
        val currentPortfolio = PortfolioDTO(listOf(
                XDTO(AssetDTO(isin = "LU1", transferrable = false), amount = AmountDTO.EUR("8")),
                XDTO(AssetDTO(isin = "LU2", transferrable = false), amount = AmountDTO.EUR("2")),
                XDTO(AssetDTO(isin = "CASH", transferrable = true), amount = AmountDTO.EUR("90"))))
        val jsonPayload = serializeRequest(assetAllocation, currentPortfolio)
        println(jsonPayload)

        val response = balancePortfolio(jsonPayload)

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
                            println(result.value)
                            assertThat(deserialize(result.value)).isEqualTo(
                                    OperationsDTO(listOf(
                                            OperationDTO(type = "Purchase", asset = AssetDTO("LU1", false), amount = AmountDTO.EUR("72.00")),
                                            OperationDTO(type = "Purchase", asset = AssetDTO("LU2", false), amount = AmountDTO.EUR("18.00"))
                                    )))
                        }
                        else -> {
                            fail("expected a Result.success")
                        }
                    }
                })
    }

    private fun serializeRequest(assetAllocation: AssetAllocationDTO, currentPortfolio: PortfolioDTO): String {
        val request = RebalanceRequest(ideal = assetAllocation, current = currentPortfolio)
        val jsonPayload = serialize(request)
        return jsonPayload
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


    private fun deserialize(get: String): OperationsDTO {
        return objectMapper.readValue<OperationsDTO>(get, OperationsDTO::class.java)
    }

    @Configuration
    class RealPortfolioRebalancer {
        @Bean
        fun portfolioRebalancer(): PortfolioRebalancer {
            return PortfolioRebalancer(FixedStrategy)
        }
    }

}

