package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

class PortfolioShould {
    @Test
    fun `add all amounts`() {
        var total = Portfolio(listOf(
                Asset(ISIN(""), Amount(BigDecimal.valueOf(1)), false),
                Asset(ISIN(""), Amount(BigDecimal.valueOf(1)), false)
        )).total()

        assertThat(total).isEqualTo(Amount(BigDecimal("2.00")))
    }

    @Test
    fun `add all amounts, with decimals`() {
        var total = Portfolio(listOf(
                Asset(ISIN(""), Amount(BigDecimal("1.1")), false),
                Asset(ISIN(""), Amount(BigDecimal("0.9")), false)
        )).total()

        assertThat(total.value.subtract(BigDecimal.valueOf(2))).isEqualTo(BigDecimal("0.00"))
    }
}