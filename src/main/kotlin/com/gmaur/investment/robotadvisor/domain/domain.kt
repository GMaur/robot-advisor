package com.gmaur.investment.robotadvisor.domain

import java.math.BigDecimal


data class Operations(val operations: List<Operation>)

open class Operation(private val asset: Asset)

class Sell(private val asset: Asset) : Operation(asset)
class Purchase(private val asset: Asset) : Operation(asset)
class Transfer(private val asset: Asset) : Operation(asset)

//TODO AGB Type union?
//TODO AGB make it typesafe
data class Portfolio(val assets: List<Any>)

data class Amount(val value: BigDecimal)

data class ISIN(val value: String)

data class Asset(val isin: ISIN, val amount: Amount)
data class TransferrableAsset(val asset: Asset)
data class AssetAllocation(val values: List<AssetAllocationSingle>) {
    fun matches(portfolio: Portfolio): Boolean {
        val firstAsset = portfolio.assets[0] as Asset
        val firstAssetPercentage = Percentage("100")
        if (firstAsset.isin == this.values[0].isin && firstAssetPercentage == this.values[0].percentage) {
            return true
        }
        return false
    }
}

data class AssetAllocationSingle(val isin: ISIN, val percentage: Percentage)
data class Percentage(val value: String)
