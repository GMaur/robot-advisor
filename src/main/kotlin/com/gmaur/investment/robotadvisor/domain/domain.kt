package com.gmaur.investment.robotadvisor.domain

import com.gmaur.investment.robotadvisor.domain.Portfolio.Companion.asAsset
import java.math.BigDecimal
import java.util.*


data class Operations(val operations: List<Operation>)

open class Operation(private val asset: Asset)

class Sell(private val asset: Asset) : Operation(asset)
class Purchase(private val asset: Asset) : Operation(asset)
class Transfer(private val asset: Asset) : Operation(asset)

//TODO AGB Type union?
//TODO AGB make it typesafe
data class Portfolio(val assets: List<Any>) {
    fun total(): Amount {
        val assets = this.assets.map { it as Asset }
        return assets.fold(
                Amount(BigDecimal.valueOf(0)),
                { amount, asset -> amount.add(asset.amount) }
        )
    }

    fun groupBy(): X {
        val temp: HashMap<ISIN, Amount> = HashMap()
        for (asset in asAsset(this.assets)) {
            temp[asset.isin] = temp.getOrDefault(asset.isin, Amount(BigDecimal.valueOf(0))).add(asset.amount)
        }
        return X(temp)
    }

    class X(private val values: Map<ISIN, Amount>) {
        fun get(isin: ISIN): Amount {
            return values[isin]!!
        }
    }

    companion object {

        public fun asAsset(x: List<Any>): List<Asset> {
            val assets = x.map { it as Asset }
            return assets
        }
    }
}

data class Amount(val value: BigDecimal) {
    fun add(amount: Amount): Amount {
        return Amount(this.value.add(amount.value))
    }

    fun percentageOf(total: Amount): Percentage {
        return Percentage(this.value.divide(total.value).toString())
    }
}

data class ISIN(val value: String)

data class Asset(val isin: ISIN, val amount: Amount)
data class TransferrableAsset(val asset: Asset)
data class AssetAllocation(val values: List<AssetAllocationSingle>) {
    fun matches(portfolio: Portfolio): Boolean {
        val assets = asAsset(portfolio.assets)
        val total = portfolio.total()
        val firstAssetPercentage = portfolio.groupBy().get(assets.first().isin).percentageOf(total)
        val sameISIN = assets.first().isin == this.values[0].isin
        val samePercentage = firstAssetPercentage == this.values[0].percentage
        if (sameISIN && samePercentage) {
            return true
        }
        return false
    }
}

data class AssetAllocationSingle(val isin: ISIN, val percentage: Percentage)
data class Percentage(val value: String)
