package com.gmaur.investment.robotadvisor

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.gmaur.investment.robotadvisor.domain.*
import com.gmaur.investment.robotadvisor.infrastructure.FileAssetAllocationRepository
import com.gmaur.investment.robotadvisor.infrastructure.FilePortfolioRepository
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal.valueOf

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [RobotAdvisorApp::class, FakeConfiguration::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RobotAdvisorAppFeature {
    @LocalServerPort
    var port: Int? = null

    private val idealRepo: FileAssetAllocationRepository = FileAssetAllocationRepository()
    private val currentRepo: FilePortfolioRepository = FilePortfolioRepository()
    private val objectMapper: ObjectMapper

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T

    @Autowired
    private lateinit var portfolioRebalancer: PortfolioRebalancer


    constructor() {
        objectMapper = ObjectMapper().registerKotlinModule()
    }

    @Before
    fun setUp() {
        FuelManager.instance.basePath = "http://localhost:" + port!!
    }

    @Test
    fun `balances a portfolio comparing to the ideal distribution`() {
        val assetAllocation = idealRepo.read()
        val currentPortfolio = currentRepo.read()

        val response = balancePortfolio(assetAllocation, currentPortfolio)

        assertThat(response.isRight())
        response.bimap(
                {
                    fail("expected a right")
                },
                { (response, result) ->
                    assertThat(deserialize(result.get())).isEqualTo(Operations(listOf()))
                    assertThat(response.statusCode).isEqualTo(200)
                    when (result) {
                        is Result.Success -> {
                            println(result.value)
                            assertThat(deserialize(result.value)).isEqualTo(
                                    Operations(listOf(
                                            Purchase(Asset(ISIN("LU1"), Amount(valueOf(72L)))),
                                            Purchase(Asset(ISIN("LU2"), Amount(valueOf(18L))))
                                    )))
                        }
                        else -> {
                            fail("expected a Result.success")
                        }
                    }
                })
        verify(portfolioRebalancer).rebalance(assetAllocation, currentPortfolio)
        // TODO AGB investigate how to argumentMatch anyOf(AssetAllocation)
        Mockito.verifyNoMoreInteractions(portfolioRebalancer)
    }

    @Test
    fun `incomplete request`() {
        val jsonPayload = """
            {"ideal":{"values":[{"isin":{"value":"LU1"},"percentage":{"value":"80"}},{"isin":{"value":"LU2"},"percentage":{"value":"20"}}]},
            "current":null}
            """ // missing the current portfolio

        val result = balancePortfolio(jsonPayload)

        assertThat(result.isRight()).isTrue()
        result.bimap(
                { fail("should be a right") },
                { (response, result) ->
                    assertThat(response.statusCode).isEqualTo(400)
                    when (result) {
                        is Result.Success -> {
                            fail("should be a failure")
                        }
                    }
                })
        Mockito.verifyZeroInteractions(portfolioRebalancer)
    }

    private fun deserialize(get: String): Operations {
        return objectMapper.readValue<Operations>(get, Operations::class.java)
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

}
