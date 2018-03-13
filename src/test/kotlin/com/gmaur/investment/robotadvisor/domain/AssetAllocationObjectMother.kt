package com.gmaur.investment.robotadvisor.domain

class AssetAllocationObjectMother {
    companion object {
        fun fund(isin: String, percentage: String): AssetAllocationSingle {
            return AssetAllocationSingle(ISIN(isin), Percentage(percentage))
        }
    }

}
