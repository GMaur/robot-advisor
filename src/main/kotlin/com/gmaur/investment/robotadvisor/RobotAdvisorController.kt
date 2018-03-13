package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.*
import com.gmaur.investment.robotadvisor.infrastructure.*
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@Configuration
@EnableAutoConfiguration
@Import(value = [RobotAdvisorController.Configuration::class])
class RobotAdvisorController(private val rebalancingStrategy: RebalancingStrategy, private val contributeStrategy: ContributeStrategy) {
    private val domainObjectMapper: DomainObjectMapper = DomainObjectMapper()

    @PostMapping("/rebalance")
    fun rebalance(@RequestBody rebalanceRequest: RebalanceRequest): Any {
        val requestOrFailure = Rebalance.parse(rebalanceRequest)
        val result = requestOrFailure.bimap(
                { it -> throw IllegalArgumentException(it[0].message) },
                { it -> Rebalance(it.current, it.ideal) })
                .toOption()
                .map { rebalance ->
                    rebalance.current.rebalance(rebalancingStrategy, rebalance.ideal)
                }
                .map { domainObjectMapper.toDTO(it) }
        return result.get()
    }

    @PostMapping("/contribute")
    fun contribute(@RequestBody requestDTO: ContributeRequest): Any {
        val requestOrFailure = Contribute.parse(requestDTO)
        val result = requestOrFailure.bimap(
                { it -> throw IllegalArgumentException(it[0].message) },
                { it -> it })
                .toOption()
                .map { x -> Portfolio(listOf(x.cash)).contribute(contributeStrategy, x.ideal) }
                .map { domainObjectMapper.toDTO(it) }
        return result.get()
    }


    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalArgumentException() {
    }

    @org.springframework.context.annotation.Configuration
    class Configuration {
        @Bean
        fun ContributeStrategy(): ContributeStrategy {
            return FixedContributeStrategy()
        }

        @Bean
        fun RebalancingStrategy(): RebalancingStrategy {
            return FixedRebalanceStrategy()
        }
    }
}
