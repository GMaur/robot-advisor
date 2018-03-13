package com.gmaur.investment.robotadvisor.domain

open class PortfolioRebalancer(private val strategy: RebalancingStrategy, private val contributeStrategy: ContributeStrategy) {
    open fun rebalance(ideal: AssetAllocation, current: Portfolio<Asset>): Operations {
        return strategy.rebalance(ideal, current)
    }

    open fun contribute(amount: Cash, ideal: AssetAllocation): Operations {
        return contributeStrategy.contribute(amount, ideal)
    }
}