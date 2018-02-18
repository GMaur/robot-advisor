package com.gmaur.investment.robotadvisor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import com.gmaur.investment.robotadvisor.infrastructure.DomainObjectMapper
import com.gmaur.investment.robotadvisor.infrastructure.Rebalance
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Configuration
@SpringBootApplication
@Import(value = [AppConfiguration::class])
@RestController
class RobotAdvisorApp(private val portfolioRebalancer: PortfolioRebalancer) : ApplicationRunner {

    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val domainObjectMapper: DomainObjectMapper = DomainObjectMapper()

    @PostMapping("/rebalance/")
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

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
    }

    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        println("Application running!")
        //TODO AGB healtcheck
    }
}

fun main(args: Array<String>) {
    runApplication<RobotAdvisorApp>(*args)
}