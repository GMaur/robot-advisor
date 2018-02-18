package com.gmaur.investment.robotadvisor.domain

import arrow.core.Either
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*


data class Operations(val operations: List<Operation>)

open class Operation(open val asset: Asset) {

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

// TODO AGB not correct that a purchase does not have an Amount?
class Purchase(override val asset: Asset) : Operation(asset) {
    override fun toString(): String {
        return "Purchase(asset=$asset)"
    }
}

data class Portfolio(val assets: List<Asset>) {
    fun total(): Amount {
        return assets
                .map(Asset::amount)
                .fold(
                Amount(BigDecimal.valueOf(0)),
                { amount, other -> amount.add(other) }
        )
    }

    fun groupBy(): GroupedAmounts {
        val temp: HashMap<ISIN, Amount> = HashMap()
        for (asset in this.assets) {
            temp[asset.isin] = temp.getOrDefault(asset.isin, Amount(BigDecimal.valueOf(0))).add(asset.amount)
        }
        return GroupedAmounts(temp)
    }

    class GroupedAmounts(private val values: Map<ISIN, Amount>) {
        fun get(isin: ISIN): Amount? {
            return values[isin]
        }
    }

    fun totalTransferrableAmount(): Amount {
        val result = this.assets
                .filter { it.transferrable }
                .map { it.amount }
                .foldRight(Amount(BigDecimal.ZERO), { a, b ->
                    a.add(b)
                })
        return result
    }
}

data class Amount(val value: BigDecimal) {
    fun add(amount: Amount): Amount {
        return Amount(value.add(amount.value).withScale())
    }

    fun percentageOf(total: Amount): Percentage {
        return Percentage(this.value.divide(total.value, MathContext(2, RoundingMode.HALF_EVEN)).withScale().toString())
    }

    fun multiply(percentage: Percentage): Amount {
        return Amount(this.value.multiply(BigDecimal(percentage.value)).withScale())
    }

    fun asString(): String {
        return this.value.withScale().toString()
    }

    private fun BigDecimal.withScale(): BigDecimal {
        return this.setScale(2)
    }
}

data class ISIN(val value: String)

data class Asset(val isin: ISIN, val amount: Amount, val transferrable: Boolean)
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
    fun matches(portfolio: Portfolio): Boolean {
        val total = portfolio.total()
        val groupedAmounts = portfolio.groupBy()
        val groupedPercentages = this.groupBy()
        //TODO AGB need to assert on teh amount of groupedAmounts = grouped assets
        for (asset in portfolio.assets) {
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

data class InvalidInvariant(val value: String) : Exception()

data class AssetAllocationSingle(val isin: ISIN, val percentage: Percentage)
data class Percentage(val value: String) {
    fun add(other: Percentage): Percentage {
        val add = BigDecimal(this.value).add(BigDecimal(other.value)).withScale()
        return Percentage(add.toEngineeringString())
    }

    fun greaterThan(percentage: Percentage): Boolean {
        return (BigDecimal(this.value).compareTo(BigDecimal(percentage.value)) == 1)
    }

    private fun BigDecimal.withScale(): BigDecimal {
        return this.setScale(2)
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
            { element: AssetAllocationSingle -> Purchase(Asset(element.isin, totalAmount.multiply(element.percentage), false)) }

}