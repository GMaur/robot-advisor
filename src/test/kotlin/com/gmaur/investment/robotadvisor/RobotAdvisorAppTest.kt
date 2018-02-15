package com.gmaur.investment.robotadvisor

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
        val idealPortfolio = idealRepo.read()
        val currentPortfolio = currentRepo.read()

        val (response, result) = balancePortfolio(idealPortfolio, currentPortfolio)

        assertThat(deserialize(result.get())).isEqualTo(Operations(listOf()))
        assertThat(response.statusCode).isEqualTo(200)
    }

    private fun deserialize(get: String): Operations {
        return objectMapper.readValue<Operations>(get, Operations::class.java)
    }

    private fun balancePortfolio(idealDistribution: AssetAllocation, currentDistribution: Portfolio): Pair<Response, Result<String, FuelError>> {
        val request = RebalanceRequest(ideal = idealDistribution, current = currentDistribution)
        val httpPost = "/rebalance/".httpPost().body(serialize(request)!!, Charsets.UTF_8).header("Content-Type" to "application/json")
        val (_, response, result) = httpPost.responseString()
        return Pair(response, result)
    }


    private fun serialize(request: RebalanceRequest): String? {
        val mapper: ObjectMapper = objectMapper
        return mapper.writeValueAsString(request)
    }

}

