package com.gmaur.investment.robotadvisor

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.gmaur.investment.robotadvisor.domain.AssetAllocation
import com.gmaur.investment.robotadvisor.domain.Operations
import com.gmaur.investment.robotadvisor.domain.Portfolio
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@SpringBootApplication
@EnableAutoConfiguration
@RestController
class RobotAdvisorApp {

    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    @Autowired
    private lateinit var portfolioRebalancer: PortfolioRebalancer

    @PostMapping("/rebalance/")
    fun rebalance(@RequestBody rebalanceRequest: RebalanceRequest): Any {
        println(rebalanceRequest)
        Rebalance.parse(rebalanceRequest).bimap(
                { it -> throw IllegalArgumentException(it[0].message) },
                { it -> Rebalance(it.current, it.ideal) })
                .map { portfolioRebalancer.rebalance(it.ideal, it.current) }

        return Operations(listOf())
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalArgumentException() {
    }

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
    }
}

data class Rebalance(val current: Portfolio, val ideal: AssetAllocation) {

    companion object {
        fun parse(rebalanceRequest: RebalanceRequest): Either<List<Error>, Rebalance> {
            if (rebalanceRequest.current != null && rebalanceRequest.ideal != null) {
                return Either.Right(Rebalance(current = rebalanceRequest.current, ideal = rebalanceRequest.ideal))
            }
            return Either.Left(listOf(Error("null values")))
        }
    }

}

fun main(args: Array<String>) {
    runApplication<RobotAdvisorApp>(*args)
}