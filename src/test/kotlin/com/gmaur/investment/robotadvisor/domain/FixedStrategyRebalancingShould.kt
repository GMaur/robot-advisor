package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FixedStrategyRebalancingShould {
    private val strategy = FixedRebalanceStrategy()

    @Test
    fun `not rebalance a portfolio that is correct already`() {
        val ideal = AssetAllocation.aNew(listOf(AssetAllocationSingle(ISIN("LU1"), Percentage("1")))).get()
        val current = Portfolio(listOf(fund("LU1", "100")))
        
        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations())
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets`() {
        val isinValue = "LU1"
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN(isinValue), Percentage("1"))
        )).get()
        val amountValue = "50"
        val current = Portfolio(listOf(
                fund(isinValue, amountValue),
                fund(isinValue, amountValue),
                fund(isinValue, amountValue)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations())
    }


    @Test
    fun `not rebalance a portfolio that is correct already, with several assets and the allocation is composed by several`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.6"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", "60"),
                fund("LU2", "40")
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations())
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets and the allocation is composed by several, order does not matter`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.6")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", "60"),
                fund("LU2", "40")
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations())
    }


    @Test
    fun `rebalance a portfolio with a single element in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("1"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", "60"),
                cash("40"),
                cash("40")
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations(
                fundPurchase("LU1", "80")))
    }

    @Test
    fun `rebalance a portfolio with multiple elements in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.5"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", "60"),
                cash("40"),
                cash("40")
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations(
                fundPurchase("LU1", "40"),
                fundPurchase("LU2", "40")
        ))
    }

    @Test
    fun `rebalance a portfolio with three elements in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU3"), Percentage("0.2"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", "60"),
                cash("40"),
                cash("40")
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations(
                fundPurchase("LU1", "32"),
                fundPurchase("LU2", "32"),
                fundPurchase("LU3", "16")
        ))
    }

    private fun operations(vararg purchases: Operation): Operations {
        return Operations(purchases.toList())
    }

    private fun fundPurchase(isin: String, value: String): Operation {
        return PurchaseObjectMother.fund(isin, value)
    }

    private fun fund(isinValue: String, amountValue: String): Asset {
        return AssetObjectMother.fund(isinValue, amountValue)
    }

    private fun cash(amountValue: String): Asset {
        return AssetObjectMother.cash(amountValue)
    }
}

