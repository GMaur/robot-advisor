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

// TODO AGB not correct that a purchase does not have an Amount?
class Purchase(override val assetDefinition: AssetDefinition, private val amount: Amount) : Operation(assetDefinition) {
    override fun amount(): Amount {
        return amount
    }

    override fun toString(): String {
        return "Purchase(assetDefinition=$assetDefinition, amount=$amount)"
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
 * Fixed mode: all transferable assets are transformed into purchases following the asset allocation.
 * It does not matter in which state (e.g., percentage) the portfolio is
 */
object FixedStrategy : RebalancingStrategy, ContributeStrategy {
    override fun contribute(amount: Cash, assetAllocation: AssetAllocation): Operations {
        val totalAmount = amount.amount
        if (totalAmount == Amount.EUR("0")) {
            return Operations(listOf())
        }

        return Operations(assetAllocation.values.map(toPurchase(totalAmount)))
    }

    override fun rebalance(assetAllocation: AssetAllocation, portfolio: Portfolio<Asset>): Operations {
        if (assetAllocation.matches(portfolio)) {
            return Operations(listOf())
        }

        val totalAmount = portfolio.cash().total()

        return Operations(assetAllocation.values.map(toPurchase(totalAmount)))
    }

    private fun toPurchase(totalAmount: Amount): (AssetAllocationSingle) -> Purchase =
            { element: AssetAllocationSingle ->
                Purchase(FundDefinition(element.isin), totalAmount.multiply(element.percentage))
            }

}