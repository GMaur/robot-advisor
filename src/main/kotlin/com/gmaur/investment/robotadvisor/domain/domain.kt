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
        fun get(isin: ISIN): Amount? {
            return values[isin]
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
        return Amount(this.value.add(amount.value).setScale(2))
    }

    fun percentageOf(total: Amount): Percentage {
        return Percentage(this.value.divide(total.value).setScale(2).toString())
    }
}

data class ISIN(val value: String)

data class Asset(val isin: ISIN, val amount: Amount)
data class TransferrableAsset(val asset: Asset)
data class AssetAllocation(val values: List<AssetAllocationSingle>) {
    fun matches(portfolio: Portfolio): Boolean {
        val assets = asAsset(portfolio.assets)
        val total = portfolio.total()
        val x = portfolio.groupBy()
        val y = this.groupBy()
        //TODO AGB need to assert on teh amount of x = grouped assets
        for (asset in assets) {
            val assetExists = x.get(asset.isin) != null
            if (!assetExists) {
                return false
            }
            val assetSamePercentage = x.get(asset.isin)!!.percentageOf(total) == y.get(asset.isin)
            if (!assetSamePercentage) {
                return false
            }
        }
        return true
    }

    private fun groupBy(): Y {
        val temp: HashMap<ISIN, Percentage> = HashMap()
        for (asset in this.values) {
            temp[asset.isin] = temp.getOrDefault(asset.isin, Percentage("0")).add(asset.percentage)
        }
        return Y(temp)
    }

    class Y(private val values: HashMap<ISIN, Percentage>) {
        fun get(isin: ISIN): Percentage? {
            return values[isin]
        }
    }
}

data class AssetAllocationSingle(val isin: ISIN, val percentage: Percentage)
data class Percentage(val value: String) {
    fun add(other: Percentage): Percentage {
        val add = BigDecimal(this.value).add(BigDecimal(other.value)).setScale(2)
        return Percentage(add.toEngineeringString())
    }
}
