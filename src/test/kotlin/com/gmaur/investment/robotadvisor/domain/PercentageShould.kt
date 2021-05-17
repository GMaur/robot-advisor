package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PercentageShould {
    @Test
    fun `add percentages with decimals`() {
        assertThat(Percentage("0.1").add(Percentage("0.1"))).isEqualTo(Percentage("0.20"))
    }

    @Test
    fun `add percentages without decimals`() {
        assertThat(Percentage("1").add(Percentage("1"))).isEqualTo(Percentage("2.00"))
    }

    @Test
    fun `allow for values bigger than 1 or 100%`() {
        assertThat(Percentage("10")).isEqualTo(Percentage("10")) // 1000%
        assertThat(Percentage("100")).isEqualTo(Percentage("100")) // 10000%
    }

}