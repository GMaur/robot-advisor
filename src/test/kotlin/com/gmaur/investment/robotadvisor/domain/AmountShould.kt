package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AmountShould {
    @Test
    fun `calculate percentage`() {
        assertPercentageOf("10", "100", "0.10")
    }

    @Test
    fun `add with decimals`() {
        val result = Amount.EUR("10.0").add(Amount.EUR("11"))

        assertThat(result).isEqualTo(Amount.EUR("21.00"))
    }

    @Test
    fun `add without decimals`() {
        val result = Amount.EUR("10").add(Amount.EUR("11"))

        assertThat(result).isEqualTo(Amount.EUR("21.00"))
    }

    @Test
    fun `calculate percentage of with decimals (rounding errors)`() {
        assertPercentageOf("21.00", "100.54", "0.21")
        assertPercentageOf("50.00", "100.54", "0.50")
        assertPercentageOf("21.00", "99.99", "0.21")
        assertPercentageOf("15.13", "218.93", "0.07")
    }

    @Test
    fun `calculate the multiplication`() {
        assertMultiply("10", ".20", "2.00")
        assertMultiply("10.000001", ".20001", "2.00")
        assertMultiply("218.93", "0.07", "15.33")
    }

    private fun assertPercentageOf(numerator: String, denominator: String, expected: String) {
        assertThat(Amount.EUR(numerator).percentageOf(Amount.EUR(denominator))).isEqualTo(Percentage(expected))
    }

    private fun assertMultiply(a: String, b: String, expected: String) {
        assertThat(Amount.EUR(a).multiply(Percentage(b))).isEqualTo(Amount.EUR(expected))
    }
}