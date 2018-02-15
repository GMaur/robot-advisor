package com.gmaur.investment.robotadvisor.infrastructure

import com.gmaur.investment.robotadvisor.domain.AssetAllocation
import com.gmaur.investment.robotadvisor.domain.AssetAllocationSingle
import com.gmaur.investment.robotadvisor.domain.ISIN
import com.gmaur.investment.robotadvisor.domain.Percentage

class FileAssetAllocationRepository {
    fun read(): AssetAllocation {
        return AssetAllocation(listOf(
                AssetAllocationSingle(ISIN("LU1"), Percentage("80")),
                AssetAllocationSingle(ISIN("LU2"), Percentage("20"))))
    }
}

