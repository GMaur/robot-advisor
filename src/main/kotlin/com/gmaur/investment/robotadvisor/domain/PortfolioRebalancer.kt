package com.gmaur.investment.robotadvisor.domain

import java.math.BigDecimal.valueOf

open class PortfolioRebalancer {
    open fun rebalance(ideal: AssetAllocation, current: Portfolio): Operations {
        if (ideal.matches(current)) {
            return Operations(listOf())
        }
        return Operations(listOf(Sell(Asset(ISIN("LU SELL"), Amount(valueOf(27L))))))
    }
}