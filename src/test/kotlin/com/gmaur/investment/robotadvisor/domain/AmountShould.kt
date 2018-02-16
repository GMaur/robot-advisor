package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

class AmountShould {
    @Test
    fun `calculate percentage`() {
        val result = Amount(BigDecimal.valueOf(10L)).percentageOf(Amount(BigDecimal.valueOf(100L)))

        assertThat(result).isEqualTo(Percentage("0.1"))
    }
}