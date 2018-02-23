package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
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

    @Test
    @Ignore("does not catch the rounding error")
    fun `calculate percentage of with decimals (rounding errors)`() {
        assertPercentageOf("21.00", "100.54", "0.21")
        assertPercentageOf("50.00", "100.54", "0.50")
        assertPercentageOf("21.00", "99.99", "0.21")


//        assertMultiply("21.00", "100.54", "2111.34")
    }

    private fun assertPercentageOf(numerator: String, denominator: String, expected: String) {
        assertThat(Amount((BigDecimal(numerator))).percentageOf(Amount(BigDecimal(denominator)))).isEqualTo(Percentage(expected))
    }

    private fun assertMultiply(a: String, b: String, expected: String) {
        assertThat(Amount((BigDecimal(a))).multiply(Percentage(b))).isEqualTo(Percentage(expected))
    }
}