package com.gmaur.investment.robotadvisor.infrastructure.alien.r4automator

import com.fasterxml.jackson.module.kotlin.readValue
import com.gmaur.investment.robotadvisor.domain.AssetObjectMother
import com.gmaur.investment.robotadvisor.domain.Portfolio
import com.gmaur.investment.robotadvisor.infrastructure.DomainObjectMapper
import com.gmaur.investment.robotadvisor.infrastructure.JSONMapper
import com.gmaur.investment.robotadvisor.infrastructure.PortfolioDTO
import org.assertj.core.api.Assertions
import org.junit.Test

class R4AutomatorFormatDeserializerTest {
    private val mapper = JSONMapper.aNew()

    @Test
    fun `parse the JSON into the domain`() {
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
        val dto = mapper.readValue<PortfolioDTO>(otherDomainJson)

        var domain = DomainObjectMapper().toDomain(dto)

        Assertions.assertThat(domain).isEqualTo(Portfolio(listOf(
                AssetObjectMother.fund("LU1050469367", "16.49"),
                AssetObjectMother.fund("LU1050470373", "16.77"),
                AssetObjectMother.fund("LU0996177134", "38.32"),
                AssetObjectMother.fund("LU0996182563", "13.87"),
                AssetObjectMother.cash("10.24")
        )))
    }
}