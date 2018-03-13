package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FixedStrategyContributeShould {
    private val strategy = FixedContributeStrategy()

    @Test
    fun `not contribute to a portfolio that has no cash available`() {
        val ideal = AssetAllocation.aNew(listOf(AssetAllocationSingle(ISIN("LU1"), Percentage("1")))).get()

        val contributions = strategy.contribute(cash(0), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not contribute to a portfolio that has no cash available - repeated allocation cases)`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5"))
        )).get()

        val contributions = strategy.contribute(cash(0), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `contribute to a portfolio with several assets -- order does matter (the order of the results is the order of the input)`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.6")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4"))
        )).get()

        val contributions = strategy.contribute(cash(100), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf(
                Purchase(
                        FundDefinition(ISIN("LU1")), Amount.EUR("60")),
                Purchase(
                        FundDefinition(ISIN("LU2")), Amount.EUR("40"))
        )))
    }

    @Test
    fun `contribute to a portfolio with several assets -- order does matter (the order of the results is the order of the input) 2`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.6")),
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.4"))
        )).get()

        val contributions = strategy.contribute(cash(100), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf(
                Purchase(
                        FundDefinition(ISIN("LU2")), Amount.EUR("60")),
                Purchase(
                        FundDefinition(ISIN("LU1")), Amount.EUR("40"))
        )))
    }


    @Test
    fun `contribute to a portfolio with a single element in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("1"))
        )).get()

        val contributions = strategy.contribute(cash(80), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf(Purchase(
                FundDefinition(ISIN("LU1")), Amount.EUR("80.00")))))
    }

    private fun fundPurchase(isin: String, value: String): Operation {
        return PurchaseObjectMother.fund(isin, value)
    }

    private fun cash(amountValue: Long): Cash {
        return Cash(Amount.EUR(amountValue.toString()))
    }
}

