package com.gmaur.investment.robotadvisor.infrastructure.alien.r4automator

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.TextNode
import com.gmaur.investment.robotadvisor.infrastructure.AmountDTO
import com.gmaur.investment.robotadvisor.infrastructure.AssetDTO
import com.gmaur.investment.robotadvisor.infrastructure.CashDTO
import com.gmaur.investment.robotadvisor.infrastructure.FundDTO
import java.io.IOException

class AssetDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<AssetDTO>(vc) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): AssetDTO {
        val node = jp.codec.readTree<TreeNode>(jp)
        val result = when (string(node, "type")) {
            "fund" -> {
                FundDTO(isin = string(node, "isin"), amount = AmountDTO.EUR(string(node, "price")))
            }
            "cash" -> {
                CashDTO(amount = AmountDTO.EUR(string(node, "value")))
            }
            else -> {
                throw IllegalArgumentException("type not recognized in: " + node.toString())
            }
        }
        return result
    }

    private fun string(node: TreeNode, key: String) = (node.get(key) as TextNode).asText()
}