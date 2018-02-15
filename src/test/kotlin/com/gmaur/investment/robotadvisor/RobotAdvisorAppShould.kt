package com.gmaur.investment.robotadvisor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import com.gmaur.investment.robotadvisor.infrastructure.FileAssetAllocationRepository
import com.gmaur.investment.robotadvisor.infrastructure.FilePortfolioRepository
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
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

    @Test
    fun `handle incorrectly requests`() {

        val idealPortfolio = idealRepo.read()
        val currentPortfolio = currentRepo.read()

        val request = RebalanceRequest(ideal = idealPortfolio, current = currentPortfolio)

        mockMvc.perform(MockMvcRequestBuilders
                .post("/rebalance")
                .content(serialize(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError)
        //TODO test that the error is a 400 - Bad request

        verifyZeroInteractions(portfolioRebalancer)
    }

    private fun serialize(request: RebalanceRequest): String? {
        val mapper: ObjectMapper = objectMapper
        return mapper.writeValueAsString(request)
    }
}


