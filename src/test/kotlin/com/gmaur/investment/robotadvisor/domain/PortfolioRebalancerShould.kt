package com.gmaur.investment.robotadvisor.domain

import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

class PortfolioRebalancerShould {
    @Test
    fun `not rebalance a portfolio that is correct already`() {
        val rebalanceRequest = RebalanceRequest(
                AssetAllocation(listOf(AssetAllocationSingle(ISIN("LU1"), Percentage("1")))),
                Portfolio(listOf(Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(100L)))))
        )

        val rebalance = PortfolioRebalancer().rebalance(rebalanceRequest.ideal!!, rebalanceRequest.current!!)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets (case 1)`() {
        val rebalanceRequest = RebalanceRequest(
                AssetAllocation(listOf(
                        AssetAllocationSingle(ISIN("LU1"), Percentage("0.5")),
                        AssetAllocationSingle(ISIN("LU1"), Percentage("0.5"))
                )),
                Portfolio(listOf(
                        Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L))),
                        Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L))),
                        Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)))
                ))
        )

        val rebalance = PortfolioRebalancer().rebalance(rebalanceRequest.ideal!!, rebalanceRequest.current!!)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets (case 2)`() {
        val rebalanceRequest = RebalanceRequest(
                AssetAllocation(listOf(
                        AssetAllocationSingle(ISIN("LU1"), Percentage("1"))
                )),
                Portfolio(listOf(
                        Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L))),
                        Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L))),
                        Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(50L)))
                ))
        )

        val rebalance = PortfolioRebalancer().rebalance(rebalanceRequest.ideal!!, rebalanceRequest.current!!)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }

    @Test
    fun `not rebalance a portfolio that is correct already, with several assets and the allocation is composed by several`() {
        val rebalanceRequest = RebalanceRequest(
                AssetAllocation(listOf(
                        AssetAllocationSingle(ISIN("LU2"), Percentage("0.4")),
                        AssetAllocationSingle(ISIN("LU1"), Percentage("0.6"))
                )),
                Portfolio(listOf(
                        Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(60L))),
                        Asset(ISIN("LU2"), Amount(BigDecimal.valueOf(40L)))
                ))
        )

        val rebalance = PortfolioRebalancer().rebalance(rebalanceRequest.ideal!!, rebalanceRequest.current!!)

        assertThat(rebalance).isEqualTo(Operations(listOf()))
    }
}