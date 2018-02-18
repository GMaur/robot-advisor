package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.FixedStrategy
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {
    @Bean
    fun portfolioRebalancer(): PortfolioRebalancer {
        return PortfolioRebalancer(FixedStrategy)
    }
}