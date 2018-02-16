package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

class PortfolioShould {
    @Test
    fun `add all amounts`() {
        var total = Portfolio(listOf(
                Asset(ISIN(""), Amount(BigDecimal.valueOf(1))),
                Asset(ISIN(""), Amount(BigDecimal.valueOf(1)))
        )).total()

        assertThat(total).isEqualTo(Amount(BigDecimal.valueOf(2)))
    }

    @Test
    fun `add all amounts, with decimals`() {
        var total = Portfolio(listOf(
                Asset(ISIN(""), Amount(BigDecimal("1.1"))),
                Asset(ISIN(""), Amount(BigDecimal("0.9")))
        )).total()

        assertThat(total.value.subtract(BigDecimal.valueOf(2))).isEqualTo(BigDecimal("0.0"))
    }
}