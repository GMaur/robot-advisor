package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.FixedContributeStrategy
import com.gmaur.investment.robotadvisor.domain.FixedRebalanceStrategy
import com.gmaur.investment.robotadvisor.infrastructure.DomainObjectMapper
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import com.gmaur.investment.robotadvisor.objectmother.RebalanceRequestObjectMother
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class RobotAdvisorControllerShould {
    private val rebalanceStrategy = Mockito.mock(FixedRebalanceStrategy::class.java)
    private val contributeStrategy = Mockito.mock(FixedContributeStrategy::class.java)

    private var robotAdvisorController: RobotAdvisorController? = null

    private val domainMapper: DomainObjectMapper = DomainObjectMapper()

    @BeforeEach
    fun setUp() {
        robotAdvisorController = RobotAdvisorController(rebalanceStrategy, contributeStrategy)
    }

    @Test
    fun `validate the rebalance request - no portfolio`() {
        assertThrows(IllegalArgumentException::class.java) {
            robotAdvisorController?.rebalance(correctRebalanceRequest.copy(current = null))

            Mockito.verifyZeroInteractions(rebalanceStrategy)
        }
    }

    @Test
    fun `validate the rebalance request - no assetallocation`() {
        assertThrows(IllegalArgumentException::class.java) {
            robotAdvisorController?.rebalance(correctRebalanceRequest.copy(ideal = null))

            Mockito.verifyZeroInteractions(rebalanceStrategy)
        }
    }

    @Test
    fun `forward the message to the portfolio rebalancer`() {
        assertThrows(IllegalArgumentException::class.java) {
            robotAdvisorController?.rebalance(correctRebalanceRequest.copy(ideal = null))

            Mockito.verify(rebalanceStrategy).rebalance(
                domainMapper.toDomain(correctRebalanceRequest.ideal!!).get(),
                domainMapper.toDomain(correctRebalanceRequest.current!!)
            )
        }
    }

    private val correctRebalanceRequest: RebalanceRequest = RebalanceRequestObjectMother.aNew()
}