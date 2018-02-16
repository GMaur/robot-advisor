package com.gmaur.investment.robotadvisor.domain

import java.math.BigDecimal.valueOf

open class PortfolioRebalancer {
    fun rebalance(ideal: AssetAllocation, current: Portfolio): Operations {
        println("pasando por el rebalancer")
        println(ideal)
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return Operations(listOf(Sell(Asset(ISIN("LU SELL"), Amount(valueOf(27L))))))
    }
}