package com.gmaur.investment.robotadvisor.domain

open class PortfolioRebalancer(private val strategy: RebalancingStrategy) {
    open fun rebalance(ideal: AssetAllocation, current: Portfolio): Operations {
        return strategy.rebalance(ideal, current)
    }
}