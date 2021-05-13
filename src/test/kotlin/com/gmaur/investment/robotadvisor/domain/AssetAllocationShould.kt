package com.gmaur.investment.robotadvisor.domain

import arrow.core.Either
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AssetAllocationShould {
    @Test
    fun `the sum of percentages should not be greater than 100%`() {
        val `element at 100%` = AssetAllocationSingle(ISIN("LU1"), percentage = Percentage("1"))
        val `another element` = AssetAllocationSingle(ISIN("LU1"), percentage = Percentage("0.2"))
        assertThat(AssetAllocation.aNew(listOf(`element at 100%`, `another element`))).isEqualTo(Either.left(InvalidInvariant("Asset Allocation percentage exceeds 100%")))
    }

    @Test
    fun `cannot repeat elements in the AssetAllocation`() {
        val result = AssetAllocation.aNew(listOf(AssetAllocationSingle(ISIN("LU1"), percentage = Percentage("0.2")), AssetAllocationSingle(ISIN("LU1"), percentage = Percentage("0.2"))))
        assertThat(result.isLeft()).isTrue()
    }
}