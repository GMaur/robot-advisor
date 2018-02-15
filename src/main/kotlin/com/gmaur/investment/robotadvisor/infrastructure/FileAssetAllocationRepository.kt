package com.gmaur.investment.robotadvisor.infrastructure

import com.gmaur.investment.robotadvisor.domain.ISIN

class FileAssetAllocationRepository {
    fun read(): AssetAllocation {
        return AssetAllocation(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("80")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("20"))))
    }
}

data class AssetAllocation(val values: List<AssetAllocationSingle>)

data class AssetAllocationSingle(val isin: ISIN, val percentage: Percentage)

data class Percentage(val value: String)
