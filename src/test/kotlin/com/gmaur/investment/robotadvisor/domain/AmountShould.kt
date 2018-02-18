package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

class AmountShould {
    @Test
    fun `calculate percentage`() {
        val result = Amount(BigDecimal.valueOf(10L)).percentageOf(Amount(BigDecimal.valueOf(100L)))

        assertThat(result).isEqualTo(Percentage("0.10"))
    }

    @Test
    fun `add with decimals`() {
        val result = Amount(BigDecimal("10.0")).add(Amount(BigDecimal("11")))

        assertThat(result).isEqualTo(Amount(BigDecimal("21.00")))
    }

    @Test
    fun `add without decimals`() {
        val result = Amount(BigDecimal("10")).add(Amount(BigDecimal("11")))

        assertThat(result).isEqualTo(Amount(BigDecimal("21.00")))
    }
}