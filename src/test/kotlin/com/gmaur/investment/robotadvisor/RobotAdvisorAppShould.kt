package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.*
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mockito
import java.math.BigDecimal

class RobotAdvisorAppShould {
    private val portfolioRebalancer: PortfolioRebalancer = Mockito.mock(PortfolioRebalancer::class.java)

    @Rule
    @JvmField
    val expectedException: ExpectedException = ExpectedException.none()

    private var robotAdvisorApp: RobotAdvisorApp? = null

    @Before
    fun setUp() {
        robotAdvisorApp = RobotAdvisorApp(portfolioRebalancer)
    }

    @Test
    fun `validate the rebalance request - no portfolio`() {
        expectedException.expect(IllegalArgumentException::class.java)

        robotAdvisorApp?.rebalance(correctRebalanceRequest.copy(current = null))

        Mockito.verifyZeroInteractions(portfolioRebalancer)
    }

    @Test
    fun `validate the rebalance request - no assetallocation`() {
        expectedException.expect(IllegalArgumentException::class.java)

        robotAdvisorApp?.rebalance(correctRebalanceRequest.copy(ideal = null))

        Mockito.verifyZeroInteractions(portfolioRebalancer)
    }

    @Test
    fun `forward the message to the portfolio rebalancer`() {
        expectedException.expect(IllegalArgumentException::class.java)

        robotAdvisorApp?.rebalance(correctRebalanceRequest.copy(ideal = null))

        Mockito.verify(portfolioRebalancer).rebalance(correctRebalanceRequest.ideal!!, correctRebalanceRequest.current!!)
    }

    private val correctRebalanceRequest: RebalanceRequest = RebalanceRequest(
            AssetAllocation(
                    listOf(AssetAllocationSingle(ISIN("LU1"), Percentage("100")))),
            Portfolio(listOf(Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(100L)))))
    )
}