package com.gmaur.investment.robotadvisor.infrastructure

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gmaur.investment.robotadvisor.infrastructure.alien.r4automator.AssetDeserializer
import java.io.IOException

class JSONMapper {
    companion object {
        fun aNew(): ObjectMapper {
            val mapper = jacksonObjectMapper()
            mapper.enable(SerializationFeature.INDENT_OUTPUT)
            val module = SimpleModule()
            module.addDeserializer(AssetDTO::class.java, AssetDeserializer())
            module.addDeserializer(X::class.java, XDeserializer())
            mapper.registerModule(module)
            return mapper
        }
    }
}

class XDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<X>(vc) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): X {
        val node = jp.codec.readTree<TreeNode>(jp)
        val result = when (string(node, "type")) {
            "fund" -> {
                XFund(isin = string(node, "isin"))
            }
            "cash" -> {
                XCash()
            }
            else -> {
                throw IllegalArgumentException("type not recognized in: " + node.toString())
            }
        }
        return result
    }

    private fun string(node: TreeNode, key: String) = (node.get(key) as TextNode).asText()
}
