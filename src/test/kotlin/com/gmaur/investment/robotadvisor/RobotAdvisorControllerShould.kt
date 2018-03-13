package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.FixedContributeStrategy
import com.gmaur.investment.robotadvisor.domain.FixedRebalanceStrategy
import com.gmaur.investment.robotadvisor.infrastructure.DomainObjectMapper
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import com.gmaur.investment.robotadvisor.objectmother.RebalanceRequestObjectMother
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mockito

class RobotAdvisorControllerShould {
    private val rebalanceStrategy = Mockito.mock(FixedRebalanceStrategy::class.java)
    private val contributeStrategy = Mockito.mock(FixedContributeStrategy::class.java)

    @Rule
    @JvmField
    val expectedException: ExpectedException = ExpectedException.none()

    private var robotAdvisorController: RobotAdvisorController? = null

    private val domainMapper: DomainObjectMapper = DomainObjectMapper()

    @Before
    fun setUp() {
        robotAdvisorController = RobotAdvisorController(rebalanceStrategy, contributeStrategy)
    }

    @Test
    fun `validate the rebalance request - no portfolio`() {
        expectedException.expect(IllegalArgumentException::class.java)

        robotAdvisorController?.rebalance(correctRebalanceRequest.copy(current = null))

        Mockito.verifyZeroInteractions(rebalanceStrategy)
    }

    @Test
    fun `validate the rebalance request - no assetallocation`() {
        expectedException.expect(IllegalArgumentException::class.java)

        robotAdvisorController?.rebalance(correctRebalanceRequest.copy(ideal = null))

        Mockito.verifyZeroInteractions(rebalanceStrategy)
    }

    @Test
    fun `forward the message to the portfolio rebalancer`() {
        expectedException.expect(IllegalArgumentException::class.java)

        robotAdvisorController?.rebalance(correctRebalanceRequest.copy(ideal = null))

        Mockito.verify(rebalanceStrategy).rebalance(domainMapper.toDomain(correctRebalanceRequest.ideal!!).get(), domainMapper.toDomain(correctRebalanceRequest.current!!))
    }

    private val correctRebalanceRequest: RebalanceRequest = RebalanceRequestObjectMother.aNew()
}