package com.gmaur.investment.robotadvisor

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
class RobotAdvisorApp

fun main(args: Array<String>) {
    runApplication<RobotAdvisorApp>(*args)
}