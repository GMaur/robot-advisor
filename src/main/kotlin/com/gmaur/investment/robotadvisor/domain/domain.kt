package com.gmaur.investment.robotadvisor.domain

import com.gmaur.investment.robotadvisor.domain.Portfolio.Companion.asAsset
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*


data class Operations(val operations: List<Operation>)

open class Operation(private val asset: Asset) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Operation

        if (asset != other.asset) return false

        return true
    }

    override fun hashCode(): Int {
        return asset.hashCode()
    }
}

class Sell(private val asset: Asset) : Operation(asset) {
    override fun toString(): String {
        return "Sell(asset=$asset)"
    }
}

class Purchase(private val asset: Asset) : Operation(asset) {
    override fun toString(): String {
        return "Purchase(asset=$asset)"
    }
}

class Transfer(private val asset: Asset) : Operation(asset) {
    override fun toString(): String {
        return "Transfer(asset=$asset)"
    }
}

//TODO AGB Type union?
//TODO AGB make it typesafe
data class Portfolio(val assets: List<Any>) {
    fun total(): Amount {
        val amounts = assets.map { it ->
            if (it is Asset) {
                it.amount
            } else if (it is TransferrableAsset) {
                it.asset.amount
            } else {
                throw IllegalArgumentException()
            }
        }
        return amounts.fold(
                Amount(BigDecimal.valueOf(0)),
                { amount, other -> amount.add(other) }
        )
    }

    fun groupBy(): GroupedAmounts {
        val temp: HashMap<ISIN, Amount> = HashMap()
        for (asset in asAsset(this.assets)) {
            temp[asset.isin] = temp.getOrDefault(asset.isin, Amount(BigDecimal.valueOf(0))).add(asset.amount)
        }
        return GroupedAmounts(temp)
    }

    class GroupedAmounts(private val values: Map<ISIN, Amount>) {
        fun get(isin: ISIN): Amount? {
            return values[isin]
        }
    }

    companion object {
        fun asAsset(x: List<Any>): List<Asset> {
            val assets = x.filter {
                it is Asset
            }.map { it as Asset }
            return assets
        }
    }

    fun totalTransferrableAmount(): Amount {
        val result = this.assets
                .filter { it is TransferrableAsset }
                .map { (it as TransferrableAsset).asset.amount }
                .foldRight(Amount(BigDecimal.ZERO), { a, b ->
                    a.add(b)
                })
        return result
    }
}

data class Amount(val value: BigDecimal) {
    fun add(amount: Amount): Amount {
        return Amount(this.value.add(amount.value).setScale(2))
    }

    fun percentageOf(total: Amount): Percentage {
        return Percentage(this.value.divide(total.value, MathContext(2, RoundingMode.HALF_EVEN)).setScale(2).toString())
    }

    fun multiply(percentage: Percentage): Amount {
        return Amount(this.value.multiply(BigDecimal(percentage.value)).setScale(2))
    }
}

data class ISIN(val value: String)

data class Asset(val isin: ISIN, val amount: Amount)
data class TransferrableAsset(val asset: Asset)
data class AssetAllocation(val values: List<AssetAllocationSingle>) {
    fun matches(portfolio: Portfolio): Boolean {
        val assets = asAsset(portfolio.assets)
        val total = portfolio.total()
        val groupedAmounts = portfolio.groupBy()
        val groupedPercentages = this.groupBy()
        //TODO AGB need to assert on teh amount of groupedAmounts = grouped assets
        for (asset in assets) {
            val assetExists = groupedAmounts.get(asset.isin) != null
            if (!assetExists) {
                return false
            }
            val assetSamePercentage = groupedAmounts.get(asset.isin)!!.percentageOf(total) == groupedPercentages.get(asset.isin)
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

interface RebalancingStrategy {
    fun rebalance(assetAllocation: AssetAllocation, portfolio: Portfolio): Operations
}

/**
 * Fixed mode: all transferrable assets are transformed into purchases following the asset allocation.
 * It does not matter in which state (e.g., percentage) the portfolio is
 */
object FixedStrategy : RebalancingStrategy {
    override fun rebalance(assetAllocation: AssetAllocation, portfolio: Portfolio): Operations {
        if (assetAllocation.matches(portfolio)) {
            return Operations(listOf())
        }

        val totalAmount = portfolio.totalTransferrableAmount()

        return Operations(assetAllocation.values.map(toPurchase(totalAmount)))
    }

    private fun toPurchase(totalAmount: Amount) =
            { element: AssetAllocationSingle -> Purchase(Asset(element.isin, totalAmount.multiply(element.percentage))) }

}