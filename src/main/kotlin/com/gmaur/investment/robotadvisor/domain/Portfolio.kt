package com.gmaur.investment.robotadvisor.domain

import com.gmaur.investment.robotadvisor.Limitations
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

data class Portfolio<out T : Asset>(val assets: List<T>) {
    fun total(): Amount {
        return assets
                .map(Asset::amount)
                .fold(
                        Amount(BigDecimal.valueOf(0)),
                        { amount, other -> amount.add(other) }
                )
    }

    fun groupBy(): GroupedAmounts {
        val temp: HashMap<AssetId, Amount> = HashMap()
        for (asset in this.funds().assets) {
            temp[asset.isin()] = temp.getOrDefault(asset.isin(), Amount(BigDecimal.valueOf(0))).add(asset.amount)
        }
        return GroupedAmounts(temp)
    }

    class GroupedAmounts(private val values: Map<AssetId, Amount>) {
        fun get(isin: AssetId): Amount? {
            return values[isin]
        }
    }

    fun cash(): Portfolio<Cash> {
        Limitations.`transferable assets are not configurable`()
        return Portfolio(this.assets
                .filter { it is Cash }
                .map { it as Cash })
    }

    fun funds(): Portfolio<FundAsset> {
        return Portfolio(this.assets
                .filter { it is FundAsset }
                .map { it as FundAsset })
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

interface Asset {
    fun amount(): Amount
}

data class ISIN(private val value: String) : AssetId {
    override fun value(): String {
        return value
    }
}

interface AssetDefinition {
    fun id(): AssetId
}

interface AssetId {
    fun value(): String
}

data class FixedAssetId(private val id: String) : AssetId {
    override fun value(): String {
        return id
    }
}

/**
 * The fund definition is the class in the OO analogy
 */
data class FundDefinition(private val isin: ISIN) : AssetDefinition {
    override fun id(): AssetId {
        return isin
    }
}

object CashDefinition : AssetDefinition {
    override fun id(): AssetId {
        return FixedAssetId("cash")
    }
}

/**
 * The fund asset is the instance in the OO analogy
 */
data class FundAsset(val fund: FundDefinition, val amount: Amount) : Asset {
    override fun amount(): Amount {
        return amount
    }

    fun isin(): AssetId {
        return fund.id()
    }
}

data class Cash(val amount: Amount) : Asset {
    override fun amount(): Amount {
        return amount
    }
}

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


