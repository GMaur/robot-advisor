package com.gmaur.investment.robotadvisor.infrastructure

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gmaur.investment.robotadvisor.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.Test
import java.io.IOException
import java.math.BigDecimal


class DomainObjectMapperShould {

    private val mapper = jacksonObjectMapper()

    init {
        val module = SimpleModule()
        module.addDeserializer(XDTO::class.java, XDeserializer())
        mapper.registerModule(module)
    }

    inner class XDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<XDTO>(vc) {

        @Throws(IOException::class, JsonProcessingException::class)
        override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): XDTO {
            val node = jp.codec.readTree<TreeNode>(jp)
            val result = when (string(node, "type")) {
                "fund" -> {
                    XDTO(AssetDTO(isin = string(node, "isin"), transferrable = false), amount = AmountDTO.EUR(string(node, "price")))
                }
                "cash" -> {
                    XDTO(AssetDTO(isin = "", transferrable = true), amount = AmountDTO.EUR(string(node, "value")))
                }
                else -> {
                    throw IllegalArgumentException("type not recognized in: " + node.toString())
                }
            }
            return result
        }

        private fun string(node: TreeNode, key: String) = (node.get(key) as TextNode).asText()
    }

    @Test
    fun `convert from domain to DTO`() {
        val ops = Operations(listOf(
                Purchase(Asset(ISIN("LU0"), Amount(BigDecimal.valueOf(0L)), false)),
                Purchase(Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(1L)), true))
        ))

        val dtos = DomainObjectMapper().toDTO(ops)

        var softly = SoftAssertions()

        softly.assertThat(dtos.operations).hasSize(2)
        softly.assertThat(dtos.operations[0].asset.isin).isEqualTo("LU0")
        softly.assertThat(dtos.operations[0].amount.value).isEqualTo("0.00")
        softly.assertThat(dtos.operations[0].amount.currency).isEqualTo("EUR")
        softly.assertThat(dtos.operations[0].type).isEqualTo("Purchase")

        softly.assertThat(dtos.operations[1].asset.isin).isEqualTo("LU1")
        softly.assertThat(dtos.operations[1].amount.value).isEqualTo("1.00")
        softly.assertThat(dtos.operations[1].amount.currency).isEqualTo("EUR")
        softly.assertThat(dtos.operations[1].type).isEqualTo("Purchase")

        softly.assertAll()
    }

    @Test
    fun `assetAllocationElementDTO - convert to domain`() {
        val assetAllocationElementDTO =
                AssetAllocationDTO(listOf(AssetAllocationElementDTO(isin = "LU1", percentage = "21%")))

        var eitherDomain = DomainObjectMapper().toDomain(assetAllocationElementDTO)

        var softly = SoftAssertions()
        softly.assertThat(eitherDomain.isRight())
        val domain = eitherDomain.get()
        softly.assertThat(domain.values).hasSize(1)
        softly.assertThat(domain.values[0].isin).isEqualTo(ISIN("LU1"))
        softly.assertThat(domain.values[0].percentage).isEqualTo(Percentage("0.21"))

        softly.assertAll()
    }

    @Test
    fun `portfolioDTO - convert to domain`() {
        val portfolioDTO = PortfolioDTO(listOf(
                XDTO(AssetDTO(isin = "LU1", transferrable = true), amount = AmountDTO.EUR("100"))))

        val domain = DomainObjectMapper().toDomain(portfolioDTO)

        var softly = SoftAssertions()

        softly.assertThat(domain.assets).hasSize(1)
        softly.assertThat(domain.assets[0].isin).isEqualTo(ISIN("LU1"))
        softly.assertThat(domain.assets[0].transferrable).isEqualTo(true)
        softly.assertThat(domain.assets[0].amount).isEqualTo(Amount(BigDecimal.valueOf(100L)))

        softly.assertAll()
    }

    @Test
    fun `parse from the other domain`() {
        val otherDomainJson = """
            {
  "assets" : [ {
    "isin" : "LU1050469367",
    "price" : "16.49",
    "type" : "fund"
  }, {
    "isin" : "LU1050470373",
    "price" : "16.77",
    "type" : "fund"
  }, {
    "isin" : "LU0996177134",
    "price" : "38.32",
    "type" : "fund"
  }, {
    "isin" : "LU0996182563",
    "price" : "13.87",
    "type" : "fund"
  }, {
    "value" : "10.24",
    "type" : "cash"
  } ]
}
"""
        val dto = mapper.readValue<PortfolioDTO>(otherDomainJson, PortfolioDTO::class.java)

        var domain = DomainObjectMapper().toDomain(dto)

        assertThat(domain).isEqualTo(Portfolio(listOf(
                Asset(ISIN("LU1050469367"), Amount(BigDecimal("16.49")), false),
                Asset(ISIN("LU1050470373"), Amount(BigDecimal("16.77")), false),
                Asset(ISIN("LU0996177134"), Amount(BigDecimal("38.32")), false),
                Asset(ISIN("LU0996182563"), Amount(BigDecimal("13.87")), false),
                Asset(ISIN(""), Amount(BigDecimal("10.24")), true)
        )))
    }
}