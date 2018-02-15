package com.gmaur.investment.robotadvisor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.gmaur.investment.robotadvisor.domain.AssetAllocation
import com.gmaur.investment.robotadvisor.domain.Operations
import com.gmaur.investment.robotadvisor.domain.Portfolio
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import com.gmaur.investment.robotadvisor.infrastructure.FileAssetAllocationRepository
import com.gmaur.investment.robotadvisor.infrastructure.FilePortfolioRepository
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.assertj.core.api.Assertions
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verifyZeroInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@RunWith(SpringRunner::class)
@SpringBootTest
@ContextConfiguration(classes = [RobotAdvisorApp::class, RobotAdvisorAppShould.FakeConfiguration::class])
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
class RobotAdvisorAppShould {

    private val objectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var portfolioRebalancer: PortfolioRebalancer

    @Configuration
    class FakeConfiguration {
        private val portfolioRebalancer = Mockito.mock(PortfolioRebalancer::class.java)

        @Bean
        fun rebalancer(): PortfolioRebalancer {
            return portfolioRebalancer
        }
    }


    constructor() {
        objectMapper = ObjectMapper().registerKotlinModule()
    }

    private val idealRepo: FileAssetAllocationRepository = FileAssetAllocationRepository()
    private val currentRepo: FilePortfolioRepository = FilePortfolioRepository()


    @Ignore
    @Test
    fun `balances a portfolio comparing to the ideal distribution`() {
        val idealPortfolio = idealRepo.read()
        val currentPortfolio = currentRepo.read()

        val (response, result) = balancePortfolio(idealPortfolio, currentPortfolio)

        Assertions.assertThat(deserialize(result.get())).isEqualTo(Operations(listOf()))
        Assertions.assertThat(response.statusCode).isEqualTo(200)
    }


    @Test
    fun `receive incorrectly requests`() {

        val idealPortfolio = idealRepo.read()
        val currentPortfolio = currentRepo.read()

        val request = RebalanceRequest(ideal = idealPortfolio, current = currentPortfolio)
        
        mockMvc.perform(MockMvcRequestBuilders
                .post("/rebalance")
                .content(serialize(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError)

        verifyZeroInteractions(portfolioRebalancer)
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


