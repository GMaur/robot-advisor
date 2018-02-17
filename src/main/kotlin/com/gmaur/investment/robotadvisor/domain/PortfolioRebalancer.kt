package com.gmaur.investment.robotadvisor.domain

open class PortfolioRebalancer {
    open fun rebalance(ideal: AssetAllocation, current: Portfolio): Operations {
        if (ideal.matches(current)) {
            return Operations(listOf())
        }

        val transferrableAssets = current.assets.filter { it is TransferrableAsset }.map { it as TransferrableAsset }

        return Operations(listOf(Purchase(Asset(ISIN(""), transferrableAssets.first().asset.amount))))
    }
}