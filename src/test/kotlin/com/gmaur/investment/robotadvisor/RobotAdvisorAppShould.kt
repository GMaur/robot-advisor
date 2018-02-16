package com.gmaur.investment.robotadvisor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import com.gmaur.investment.robotadvisor.infrastructure.FileAssetAllocationRepository
import com.gmaur.investment.robotadvisor.infrastructure.FilePortfolioRepository
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verifyZeroInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@RunWith(SpringRunner::class)
@SpringBootTest
@ContextConfiguration(classes = [RobotAdvisorApp::class, FakeConfiguration::class])
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
class RobotAdvisorAppShould {

    private val objectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var portfolioRebalancer: PortfolioRebalancer


    constructor() {
        objectMapper = ObjectMapper().registerKotlinModule()
    }

    private val idealRepo: FileAssetAllocationRepository = FileAssetAllocationRepository()
    private val currentRepo: FilePortfolioRepository = FilePortfolioRepository()

    @Test
    fun `handle incorrect requests - empty body`() {

        val idealPortfolio = idealRepo.read()
        val currentPortfolio = currentRepo.read()

        val request = RebalanceRequest(ideal = idealPortfolio, current = currentPortfolio)

        mockMvc.perform(post("/rebalance")
                .content(serialize(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError)
                .andExpect(status().isNotFound)

        verifyZeroInteractions(portfolioRebalancer)
    }


    @Ignore("this test, using mockmvc, has a different result than using the real mvc")
    @Test
    fun `handle incorrect requests - can't build a proper request`() {
        val jsonPayload = """
            {"ideal":{"values":[{"isin":{"value":"LU1"},"percentage":{"value":"80"}},{"isin":{"value":"LU2"},"percentage":{"value":"20"}}]},
            "current":null}
            """ // missing the current portfolio

        mockMvc.perform(post("/rebalance")
                .content(jsonPayload)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError)
                .andExpect(status().isBadRequest)

        verifyZeroInteractions(portfolioRebalancer)
    }

    private fun serialize(request: RebalanceRequest): String? {
        return objectMapper.writeValueAsString(request)
    }

    private fun deserialize(request: String): Any {
        return objectMapper.readValue(request, RebalanceRequest::class.java)
    }
}


