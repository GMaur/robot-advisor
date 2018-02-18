package com.gmaur.investment.robotadvisor.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

class PortfolioRebalancerShould {
    private val portfolioRebalancer = PortfolioRebalancer()

    @Test
    fun `not rebalance a portfolio that is correct already`() {
        val ideal = AssetAllocation(listOf(AssetAllocationSingle(ISIN("LU1"), Percentage("1"))))
        val current = Portfolio(listOf(Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(100L)))))


        val rebalance = portfolioRebalancer.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets - repeated allocation cases)`() {
        val ideal = AssetAllocation(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5"))
        ))
        val current = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L))),
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L))),
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)))
        ))

        val rebalance = portfolioRebalancer.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets - no repeated allocation cases`() {
        val ideal = AssetAllocation(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("1"))
        ))
        val current = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L))),
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L))),
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)))
        ))

        val rebalance = portfolioRebalancer.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets and the allocation is composed by several`() {
        val ideal = AssetAllocation(listOf(
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4")),
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.6"))
        ))
        val current = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L))),
                Asset(ISIN("LU2"), Amount(BigDecimal.valueOf(40L)))
        ))

        val rebalance = portfolioRebalancer.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets and the allocation is composed by several, order does not matter`() {
        val ideal = AssetAllocation(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.6")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.4"))
        ))
        val current = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L))),
                Asset(ISIN("LU2"), Amount(BigDecimal.valueOf(40L)))
        ))

        val rebalance = portfolioRebalancer.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }


    @Test
    fun `rebalance a portfolio with a single element in the asset allocation`() {
        val ideal = AssetAllocation(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("1"))
        ))
        val current = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L))),
                TransferrableAsset(Asset(ISIN("LU2"), Amount(BigDecimal.valueOf(40L)))),
                TransferrableAsset(Asset(ISIN("LU2"), Amount(BigDecimal.valueOf(40L))))
        ))

        val rebalance = portfolioRebalancer.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf(Purchase(Asset(ISIN("LU1"), Amount(BigDecimal("80.00")))))))
    }

    @Test
    fun `(fixed mode) rebalance a portfolio with multiple elements in the asset allocation`() {
        val ideal = AssetAllocation(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("0.5"))
        ))
        val current = Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L))),
                TransferrableAsset(Asset(ISIN(""), Amount(BigDecimal.valueOf(40L)))),
                TransferrableAsset(Asset(ISIN(""), Amount(BigDecimal.valueOf(40L))))
        ))

        val rebalance = portfolioRebalancer.rebalance(ideal, current)

        assertThat(rebalance).isEqualTo(Operations(listOf(
                Purchase(Asset(ISIN("LU1"), Amount(BigDecimal("40.00")))),
                Purchase(Asset(ISIN("LU2"), Amount(BigDecimal("40.00"))))
        )))
    }
}