package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.*
import com.gmaur.investment.robotadvisor.infrastructure.RebalanceRequest
import java.math.BigDecimal

class RebalanceRequestObjectMother {
    companion object {
        fun aNew(): RebalanceRequest {
            return RebalanceRequest(
                    AssetAllocation.aNew(
                            listOf(AssetAllocationSingle(ISIN("LU1"), Percentage("1")))).get(),
                    Portfolio(listOf(Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(100L)), false)))
            )
        }
    }
}