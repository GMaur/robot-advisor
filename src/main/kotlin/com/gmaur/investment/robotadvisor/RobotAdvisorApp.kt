package com.gmaur.investment.robotadvisor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.gmaur.investment.robotadvisor.domain.Operations
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableAutoConfiguration
@RestController
class RobotAdvisorApp {

    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    @PostMapping("/rebalance/")
    fun rebalance(
//            @RequestParam(value = "ideal") ideal: Portfolio,
//            @RequestParam(value = "current") current: Portfolio
            @RequestBody(required = true) body: RebalanceRequest
    ): Any {
//        val x = mapper.readValue<RebalanceRequest>(body, RebalanceRequest::class.java)
//        println(ideal)
//        println(current)
        println(body)
        return Operations(listOf())
    }

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
    }
}

fun main(args: Array<String>) {
    runApplication<RobotAdvisorApp>(*args)
}