package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FixedStrategyContributeShould {
    private val strategy = FixedStrategy

    @Test
    fun `not rebalance a portfolio that is correct already`() {
        val ideal = AssetAllocation.aNew(listOf(AssetAllocationSingle(ISIN("LU1"), Percentage("1")))).get()

        val rebalance = strategy.contribute(cash(0), ideal)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets - repeated allocation cases)`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5"))
        )).get()

        val rebalance = strategy.contribute(cash(0), ideal)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets - no repeated allocation cases`() {
        val isinValue = "LU1"
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN(isinValue), Percentage("1"))
        )).get()

        val rebalance = strategy.contribute(cash(0), ideal)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }


    @Test
    fun `not rebalance a portfolio that is correct already, with several assets and the allocation is composed by several`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.6"))
        )).get()

        val rebalance = strategy.contribute(cash(0), ideal)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets and the allocation is composed by several, order does not matter`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.6")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4"))
        )).get()

        val rebalance = strategy.contribute(cash(100), ideal)

        assertThat(rebalance).isEqualTo(Operations(listOf(
                Purchase(
                        FundDefinition(ISIN("LU1")), Amount.EUR("60")),
                Purchase(
                        FundDefinition(ISIN("LU2")), Amount.EUR("40"))
        )))
    }


    @Test
    fun `rebalance a portfolio with a single element in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("1"))
        )).get()

        val rebalance = strategy.contribute(cash(80), ideal)

        assertThat(rebalance).isEqualTo(Operations(listOf(Purchase(
                FundDefinition(ISIN("LU1")), Amount.EUR("80.00")))))
    }

    @Test
    fun `rebalance a portfolio with multiple elements in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.5"))
        )).get()

        val rebalance = strategy.contribute(cash(80), ideal)

        assertThat(rebalance).isEqualTo(Operations(listOf(
                fundPurchase("LU1", "40.00"),
                fundPurchase("LU2", "40.00")
        )))
    }

    @Test
    fun `rebalance a portfolio with three elements in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU3"), Percentage("0.2"))
        )).get()

        val rebalance = strategy.contribute(cash(80L), ideal)

        assertThat(rebalance).isEqualTo(Operations(listOf(
                fundPurchase("LU1", "32.00"),
                fundPurchase("LU2", "32.00"),
                fundPurchase("LU3", "16.00")
        )))
    }

    private fun fundPurchase(isin: String, value: String): Operation {
        return PurchaseObjectMother.fund(isin, value)
    }

    private fun cash(amountValue: Long): Cash {
        return Cash(Amount.EUR(amountValue.toString()))
    }
}

