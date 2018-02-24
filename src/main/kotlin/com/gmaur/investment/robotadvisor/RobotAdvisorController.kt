package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import com.gmaur.investment.robotadvisor.infrastructure.DomainObjectMapper
import com.gmaur.investment.robotadvisor.infrastructure.Rebalance
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@Configuration
@EnableAutoConfiguration
class RobotAdvisorController(private val portfolioRebalancer: PortfolioRebalancer) {

    private val domainObjectMapper: DomainObjectMapper = DomainObjectMapper()

    @PostMapping("/rebalance")
    fun rebalance(@RequestBody rebalanceRequest: RebalanceRequest): Any {
        val requestOrFailure = Rebalance.parse(rebalanceRequest)
        val rebalance = requestOrFailure.bimap(
                { it -> throw IllegalArgumentException(it[0].message) },
                { it -> Rebalance(it.current, it.ideal) })
                .toOption()
        val result = rebalance
                .map { portfolioRebalancer.rebalance(it.ideal, it.current) }
                .map { domainObjectMapper.toDTO(it) }
        return result.get()
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalArgumentException() {
    }
}