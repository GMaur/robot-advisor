package com.gmaur.investment.robotadvisor.infrastructure.alien.r4automator

import com.fasterxml.jackson.module.kotlin.readValue
import com.gmaur.investment.robotadvisor.domain.Amount
import com.gmaur.investment.robotadvisor.domain.Asset
import com.gmaur.investment.robotadvisor.domain.ISIN
import com.gmaur.investment.robotadvisor.domain.Portfolio
import com.gmaur.investment.robotadvisor.infrastructure.DomainObjectMapper
import com.gmaur.investment.robotadvisor.infrastructure.JSONMapper
import com.gmaur.investment.robotadvisor.infrastructure.PortfolioDTO
import org.assertj.core.api.Assertions
import org.junit.Test
import java.math.BigDecimal

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
                Asset(ISIN("LU1050469367"), Amount(BigDecimal("16.49")), false),
                Asset(ISIN("LU1050470373"), Amount(BigDecimal("16.77")), false),
                Asset(ISIN("LU0996177134"), Amount(BigDecimal("38.32")), false),
                Asset(ISIN("LU0996182563"), Amount(BigDecimal("13.87")), false),
                Asset(ISIN(""), Amount(BigDecimal("10.24")), true)
        )))
    }
}