package com.gmaur.investment.robotadvisor

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.gmaur.investment.robotadvisor.domain.AssetAllocation
import com.gmaur.investment.robotadvisor.domain.Portfolio
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import com.gmaur.investment.robotadvisor.infrastructure.OperationMapper
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Configuration
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = arrayOf("com.gmaur.investment.robotadvisor"))
@RestController
class RobotAdvisorApp(private val portfolioRebalancer: PortfolioRebalancer) : ApplicationRunner {

    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val operationMapper: OperationMapper = OperationMapper()

    @PostMapping("/rebalance/")
    fun rebalance(@RequestBody rebalanceRequest: RebalanceRequest): Any {
        val parse = Rebalance.parse(rebalanceRequest)
        val bimap = parse.bimap(
                { it -> throw IllegalArgumentException(it[0].message) },
                { it -> Rebalance(it.current, it.ideal) })
        val map = bimap
                .map { portfolioRebalancer.rebalance(it.ideal, it.current) }
                .map { operationMapper.map(it) }
        val x = map
        val get = x.get()
        return get
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalArgumentException() {
    }

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
    }

    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        println("Application running!")
        //TODO AGB healtcheck
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