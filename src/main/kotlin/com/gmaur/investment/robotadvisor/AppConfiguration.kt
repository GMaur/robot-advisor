package com.gmaur.investment.robotadvisor

import com.fasterxml.jackson.databind.ObjectMapper
import com.gmaur.investment.robotadvisor.domain.FixedStrategy
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import com.gmaur.investment.robotadvisor.infrastructure.JSONMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {
    @Bean
    fun portfolioRebalancer(): PortfolioRebalancer {
        return PortfolioRebalancer(FixedStrategy)
    }

    @Bean
    fun deserializer(): ObjectMapper {
        return JSONMapper.aNew()
    }
}