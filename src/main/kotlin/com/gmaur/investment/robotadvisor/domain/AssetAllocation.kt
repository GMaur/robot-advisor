package com.gmaur.investment.robotadvisor.domain

import arrow.core.Either
import java.util.*

data class AssetAllocation private constructor(val values: List<AssetAllocationSingle>) {
    companion object {
        fun aNew(values: List<AssetAllocationSingle>): Either<Exception, AssetAllocation> {
            val totalPercentage = sum(values)
            if (totalPercentage.greaterThan(Percentage("1"))) {
                return Either.left(InvalidInvariant("Asset Allocation percentage exceeds 100%"))
            } else {
                return Either.right(AssetAllocation(values))
            }
        }

        private fun sum(values: List<AssetAllocationSingle>): Percentage {
            return values.foldRight(Percentage("0"), { a, b ->
                a.percentage.add(b)
            })
        }
    }

    fun matches(portfolio: Portfolio<Asset>): Boolean {
        val total = portfolio.total()
        val groupedAmounts = portfolio.groupBy()
        val groupedPercentages = this.groupBy()
        //TODO AGB need to assert on teh amount of groupedAmounts = grouped assets
        for (asset in portfolio.funds().assets) {
            val assetExists = groupedAmounts.get(asset.isin()) != null
            if (!assetExists) {
                return false
            }
            val assetSamePercentage = groupedAmounts.get(asset.isin())!!.percentageOf(total) == groupedPercentages.get(asset.isin())
            if (!assetSamePercentage) {
                return false
            }
        }
        return true
    }

    private fun groupBy(): GroupedPercentages {
        val temp: HashMap<ISIN, Percentage> = HashMap()
        for (asset in this.values) {
            temp[asset.isin] = temp.getOrDefault(asset.isin, Percentage("0")).add(asset.percentage)
        }
        return GroupedPercentages(temp)
    }

    class GroupedPercentages(private val values: HashMap<ISIN, Percentage>) {
        fun get(id: AssetId): Percentage? {
            return values[id]
        }
    }
}

data class AssetAllocationSingle(val isin: ISIN, val percentage: Percentage)