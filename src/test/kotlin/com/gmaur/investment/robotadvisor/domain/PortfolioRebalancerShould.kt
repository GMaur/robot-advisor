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
    fun `not rebalance a portfolio that is correct already, with several assets`() {
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
}