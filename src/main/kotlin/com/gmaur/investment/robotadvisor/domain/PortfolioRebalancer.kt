package com.gmaur.investment.robotadvisor.domain

open class PortfolioRebalancer(private val fixedStrategy: RebalancingStrategy) {
    open fun rebalance(ideal: AssetAllocation, current: Portfolio): Operations {
        return fixedStrategy.rebalance(ideal, current)
    }
}