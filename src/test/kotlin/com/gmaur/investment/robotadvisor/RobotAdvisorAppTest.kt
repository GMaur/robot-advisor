package com.gmaur.investment.robotadvisor

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.gmaur.investment.robotadvisor.domain.AssetAllocation
import com.gmaur.investment.robotadvisor.domain.Operations
import com.gmaur.investment.robotadvisor.domain.Portfolio
import com.gmaur.investment.robotadvisor.infrastructure.FileAssetAllocationRepository
import com.gmaur.investment.robotadvisor.infrastructure.FilePortfolioRepository
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RobotAdvisorAppTest {
    @LocalServerPort
    var port: Int? = null

    private val idealRepo: FileAssetAllocationRepository = FileAssetAllocationRepository()
    private val currentRepo: FilePortfolioRepository = FilePortfolioRepository()
    private val objectMapper: ObjectMapper

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
                })
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

