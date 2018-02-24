package com.gmaur.investment.robotadvisor

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import kotlin.system.exitProcess

@Configuration
@ComponentScan("com.gmaur.investment.robotadvisor")
@Import(value = [AppConfiguration::class])
@EnableAutoConfiguration
class MainApplication : ApplicationRunner {
    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        println("Application running!")
        //TODO AGB healtcheck
    }
}

fun main(args: Array<String>) {
    if (!inDebugMode()) {
        println("This application is in alpha mode - please execute it in debug mode only")
        exitProcess(1)
    }
    runApplication<MainApplication>(*args)
}

fun inDebugMode(): Boolean {
    return java.lang.management.ManagementFactory.getRuntimeMXBean().inputArguments.toString().contains("-agentlib:jdwp")
}
