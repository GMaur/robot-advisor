package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

class FixedStrategyShould {
    private val strategy = FixedStrategy

    @Test
    fun `not rebalance a portfolio that is correct already`() {
        val ideal = AssetAllocation.aNew(listOf(AssetAllocationSingle(ISIN("LU1"), Percentage("1")))).get()
        val current = Portfolio(listOf(Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(100L)), false)))


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
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)), false),
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)), false),
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)), false)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets - no repeated allocation cases`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("1"))
        )).get()
        val current = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)), false),
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)), false),
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)), false)
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
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L)), false),
                Asset(ISIN("LU2"), Amount(BigDecimal.valueOf(40L)), false)
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
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L)), false),
                Asset(ISIN("LU2"), Amount(BigDecimal.valueOf(40L)), false)
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
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L)), false),
                Asset(ISIN("LU2"), Amount(BigDecimal.valueOf(40L)), true),
                Asset(ISIN("LU2"), Amount(BigDecimal.valueOf(40L)), true)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf(Purchase(Asset(ISIN("LU1"), Amount(BigDecimal("80.00")), false)))))
    }

    @Test
    fun `rebalance a portfolio with multiple elements in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.5"))
        )).get()
        val current = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L)), false),
                Asset(ISIN(""), Amount(BigDecimal.valueOf(40L)), true),
                Asset(ISIN(""), Amount(BigDecimal.valueOf(40L)), true)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf(
                Purchase(Asset(ISIN("LU1"), Amount(BigDecimal("40.00")), false)),
                Purchase(Asset(ISIN("LU2"), Amount(BigDecimal("40.00")), false))
        )))
    }

    @Test
    fun `rebalance a portfolio with three elements in the asset allocation`() {
        val ideal = AssetAllocation.aNew(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU3"), Percentage("0.2"))
        )).get()
        val current = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L)), false),
                Asset(ISIN(""), Amount(BigDecimal.valueOf(40L)), true),
                Asset(ISIN(""), Amount(BigDecimal.valueOf(40L)), true)
        ))

        val rebalance = strategy.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf(
                Purchase(Asset(ISIN("LU1"), Amount(BigDecimal("32.00")), false)),
                Purchase(Asset(ISIN("LU2"), Amount(BigDecimal("32.00")), false)),
                Purchase(Asset(ISIN("LU3"), Amount(BigDecimal("16.00")), false))
        )))
    }
}

