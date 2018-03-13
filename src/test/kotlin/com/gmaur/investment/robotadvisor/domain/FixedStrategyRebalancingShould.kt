package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FixedStrategyRebalancingShould {
    private val strategy = FixedStrategy

    @Test
    fun `not rebalance a portfolio that is correct already`() {
        val ideal = AssetAllocation.aNew(listOf(AssetAllocationSingle(ISIN("LU1"), Percentage("1")))).get()
        val current = Portfolio(listOf(fund("LU1", 100L)))


        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets - repeated allocation cases)`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", 50),
                fund("LU1", 50L),
                fund("LU1", 50)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets - no repeated allocation cases`() {
        val isinValue = "LU1"
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN(isinValue), Percentage("1"))
        )).get()
        val amountValue = 50L
        val current = Portfolio(listOf(
                fund(isinValue, amountValue),
                fund(isinValue, amountValue),
                fund(isinValue, amountValue)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }


    @Test
    fun `not rebalance a portfolio that is correct already, with several assets and the allocation is composed by several`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.6"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", 60L),
                fund("LU2", 40L)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets and the allocation is composed by several, order does not matter`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.6")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", 60L),
                fund("LU2", 40L)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }


    @Test
    fun `rebalance a portfolio with a single element in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("1"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", 60L),
                cash(40),
                cash(40)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations(fundPurchase("LU1", "80")))
    }

    @Test
    fun `rebalance a portfolio with multiple elements in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.5"))
        )).get()
        val current = Portfolio(listOf(
                fund("LU1", 60L),
                cash(40),
                cash(40)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations(
                fundPurchase("LU1", "40.00"),
                fundPurchase("LU2", "40.00")
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
                fund("LU1", 60L),
                cash(40),
                cash(40)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(operations(
                fundPurchase("LU1", "32.00"),
                fundPurchase("LU2", "32.00"),
                fundPurchase("LU3", "16.00")
        ))
    }

    private fun operations(vararg purchases: Operation): Operations {
        return Operations(purchases.toList())
    }

    private fun fundPurchase(isin: String, value: String): Operation {
        return PurchaseObjectMother.fund(isin, value)
    }

    private fun fund(isinValue: String, amountValue: Long): Asset {
        return AssetObjectMother.fund(isinValue, amountValue)
    }

    private fun cash(amountValue: Long): Asset {
        return AssetObjectMother.cash(amountValue)
    }
}

