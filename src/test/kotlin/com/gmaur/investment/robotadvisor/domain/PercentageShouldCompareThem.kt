package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PercentageShouldCompareThem {

    @Test
    fun `test for greaterThan (positive)`() {
        Assertions.assertThat(Percentage("1").greaterThan(Percentage("0.99"))).isTrue()
        Assertions.assertThat(Percentage("1").greaterThan(Percentage("0.99999999"))).isTrue()
        Assertions.assertThat(Percentage("1").greaterThan(Percentage("0.98"))).isTrue()
        Assertions.assertThat(Percentage("1").greaterThan(Percentage("0.90"))).isTrue()
    }

    @Test
    fun `test for greaterThan (equals)`() {
        Assertions.assertThat(Percentage("1").greaterThan(Percentage("1"))).isFalse()
        Assertions.assertThat(Percentage("1").greaterThan(Percentage("1.0"))).isFalse()
        Assertions.assertThat(Percentage("1").greaterThan(Percentage("1.00"))).isFalse()
    }

    @Test
    fun `test for greaterThan (false)`() {
        Assertions.assertThat(Percentage("1").greaterThan(Percentage("1.01"))).isFalse()
        Assertions.assertThat(Percentage("1").greaterThan(Percentage("1.001"))).isFalse()
    }
}