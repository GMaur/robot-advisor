package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FixedStrategyContributeShould {
    private val strategy = FixedContributeStrategy()

    @Test
    fun `not contribute to a portfolio that has no cash available`() {
        val ideal = AssetAllocation.aNew(listOf(fundAsset("LU1", "1"))).get()

        val contributions = strategy.contribute(cash(0), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not contribute to a portfolio that has no cash available - repeated allocation cases)`() {
        val ideal = AssetAllocation.aNew(listOf(
                fundAsset("LU1", "0.5"),
                fundAsset("LU1", "0.5")
        )).get()

        val contributions = strategy.contribute(cash(0), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `contribute to a portfolio with several assets -- order does matter (the order of the results is the order of the input)`() {
        val ideal = AssetAllocation.aNew(listOf(
                fundAsset("LU1", "0.6"),
                fundAsset("LU2", "0.4")
        )).get()

        val contributions = strategy.contribute(cash(100), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf(
                fundPurchase("LU1", "60"),
                fundPurchase("LU2", "40"))
        ))
    }

    @Test
    fun `contribute to a portfolio with several assets -- order does matter (the order of the results is the order of the input) 2`() {
        val ideal = AssetAllocation.aNew(listOf(
                fundAsset("LU2", "0.6"),
                fundAsset("LU1", "0.4")
        )).get()

        val contributions = strategy.contribute(cash(100), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf(
                fundPurchase("LU2", "60"),
                fundPurchase("LU1", "40"))
        ))
    }


    @Test
    fun `contribute to a portfolio with a single element in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                fundAsset("LU1", "1")
        )).get()

        val contributions = strategy.contribute(cash(80), ideal)

        assertThat(contributions).isEqualTo(Operations(listOf(Purchase(
                FundDefinition(ISIN("LU1")), Amount.EUR("80.00")))))
    }

    private fun fundAsset(isin: String, percentage: String) = AssetAllocationObjectMother.fund(isin, percentage)

    private fun fundPurchase(isin: String, value: String): Operation {
        return PurchaseObjectMother.fund(isin, value)
    }

    private fun cash(amountValue: Long): Cash {
        return Cash(Amount.EUR(amountValue.toString()))
    }
}

