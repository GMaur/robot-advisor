package com.gmaur.investment.robotadvisor.domain

import com.gmaur.investment.robotadvisor.domain.AssetObjectMother.Companion.cash
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

class PortfolioShould {
    @Test
    fun `add all amounts`() {
        var total = Portfolio(listOf(
                AssetObjectMother.cash(1),
                AssetObjectMother.cash(1)

        )).total()

        assertThat(total).isEqualTo(Amount(BigDecimal("2.00")))
    }

    @Test
    fun `add all amounts, with decimals`() {
        var total = Portfolio(listOf(
                cash("1.1"),
                cash("0.9")
        )).total()

        assertThat(total.value.subtract(BigDecimal.valueOf(2))).isEqualTo(BigDecimal("0.00"))
    }

}

