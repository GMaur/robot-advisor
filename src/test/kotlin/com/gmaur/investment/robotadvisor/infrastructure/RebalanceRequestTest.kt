package com.gmaur.investment.robotadvisor.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RebalanceRequestTest {
    @Test
    fun `with nullable attributes`() {
        val rebalanceRequest = RebalanceRequest(null, null)
        assertThat(rebalanceRequest).isNotNull()
    }
}