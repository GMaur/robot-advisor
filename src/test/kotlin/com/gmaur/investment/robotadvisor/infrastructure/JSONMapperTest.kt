package com.gmaur.investment.robotadvisor.infrastructure

import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class JSONMapperTest {
    @Test
    fun `parse an ideal portfolio`() {
        val json = """
         {"assets": [
    {
      "isin": "LU1050469367",
      "price": "1.49",
      "type": "fund"
    },
    {
      "isin": "LU1050470373",
      "price": "0.77",
      "type": "fund"
    },
    {
      "isin": "LU0996177134",
      "price": "3.0",
      "type": "fund"
    },
    {
      "isin": "LU0996182563",
      "price": "0.87",
      "type": "fund"
    },
    {
      "value": "1.24",
      "type": "cash"
    }
  ]}
"""
        val dtos = JSONMapper.aNew().readValue<PortfolioDTO>(json)
        assertThat(dtos.assets).hasSize(5)
        assertThat(dtos.assets[0]).isEqualTo(FundDTO("LU1050469367", "1.49"))
    }
}