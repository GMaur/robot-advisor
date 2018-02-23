package com.gmaur.investment.robotadvisor.objectmother

import com.gmaur.investment.robotadvisor.infrastructure.*

class RebalanceRequestObjectMother {
    companion object {
        fun aNew(): RebalanceRequest {
            return RebalanceRequest(
                    AssetAllocationDTO(listOf(
                            AssetAllocationElementDTO(isin = "LU1", percentage = "1%"))),
                    PortfolioDTO(listOf(
                            FundDTO(isin = "LU1", price = "100")))
            )
        }
    }
}