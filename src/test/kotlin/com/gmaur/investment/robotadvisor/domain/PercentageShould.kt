package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PercentageShould {
    @Test
    fun `add percentages with decimals`() {
        assertThat(Percentage("0.1").add(Percentage("0.1"))).isEqualTo(Percentage("0.20"))
    }

    @Test
    fun `add percentages without decimals`() {
        assertThat(Percentage("1").add(Percentage("1"))).isEqualTo(Percentage("2.00"))
    }
}