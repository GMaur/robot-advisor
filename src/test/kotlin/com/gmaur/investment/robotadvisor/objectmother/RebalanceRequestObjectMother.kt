package com.gmaur.investment.robotadvisor.objectmother

import com.gmaur.investment.robotadvisor.infrastructure.*

class RebalanceRequestObjectMother {
    companion object {
        fun aNew(): RebalanceRequest {
            return RebalanceRequest(
                    AssetAllocationDTO(listOf(
                            AssetAllocationElementDTO(isin = "LU1", percentage = "1%"))),
                    PortfolioDTO(listOf(
                            XDTO(AssetDTO(isin = "LU1", transferrable = false), amount = AmountDTO.EUR("100"))))
            )
        }
    }
}