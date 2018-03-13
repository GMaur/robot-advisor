package com.gmaur.investment.robotadvisor.domain


data class Operations(val operations: List<Operation>)

abstract class Operation(open val assetDefinition: AssetDefinition) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Operation

        if (assetDefinition != other.assetDefinition) return false

        return true
    }

    override fun hashCode(): Int {
        return assetDefinition.hashCode()
    }

    abstract fun amount(): Amount

}

class Purchase(override val assetDefinition: AssetDefinition, private val amount: Amount) : Operation(assetDefinition) {
    override fun amount(): Amount {
        return amount
    }

    override fun toString(): String {
        return "Purchase(assetDefinition=$assetDefinition, amount=$amount)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Purchase

        if (assetDefinition != other.assetDefinition) return false
        if (amount != other.amount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + assetDefinition.hashCode()
        result = 31 * result + amount.hashCode()
        return result
    }


}

data class InvalidInvariant(val value: String) : Exception()

interface RebalancingStrategy {
    fun rebalance(assetAllocation: AssetAllocation, portfolio: Portfolio<Asset>): Operations
}

interface ContributeStrategy {
    fun contribute(amount: Cash, assetAllocation: AssetAllocation): Operations
}

/**
 * Fixed mode: cash is transformed into purchases following the asset allocation.
 */
open class FixedContributeStrategy : ContributeStrategy {
    override fun contribute(amount: Cash, assetAllocation: AssetAllocation): Operations {
        val totalAmount = amount.amount
        if (totalAmount == Amount.EUR("0")) {
            return Operations(listOf())
        }

        return Operations(assetAllocation.values.map(toPurchase(totalAmount)))
    }
}

/**
 * Fixed mode: TODO
 */
open class FixedRebalanceStrategy : RebalancingStrategy {

    override fun rebalance(assetAllocation: AssetAllocation, portfolio: Portfolio<Asset>): Operations {
        if (assetAllocation.matches(portfolio)) {
            return Operations(listOf())
        }

        val totalAmount = portfolio.cash().total()

        return Operations(assetAllocation.values.map(toPurchase(totalAmount)))
    }
}

private fun toPurchase(totalAmount: Amount): (AssetAllocationSingle) -> Purchase =
        { element: AssetAllocationSingle ->
            Purchase(FundDefinition(element.isin), totalAmount.multiply(element.percentage))
        }
