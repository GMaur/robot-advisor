package com.gmaur.investment.robotadvisor.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gmaur.investment.robotadvisor.infrastructure.alien.r4automator.XDeserializer

class JSONMapper {
    companion object {
        fun aNew(): ObjectMapper {
            val mapper = jacksonObjectMapper()
            mapper.enable(SerializationFeature.INDENT_OUTPUT)
            val module = SimpleModule()
            module.addDeserializer(XDTO::class.java, XDeserializer())
            mapper.registerModule(module)
            return mapper
        }
    }
}