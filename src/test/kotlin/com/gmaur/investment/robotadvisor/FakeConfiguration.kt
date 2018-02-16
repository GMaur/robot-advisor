package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FakeConfiguration {
    private val portfolioRebalancer = Mockito.mock(PortfolioRebalancer::class.java)

    @Bean
    fun rebalancer(): PortfolioRebalancer {
        return portfolioRebalancer
    }
}