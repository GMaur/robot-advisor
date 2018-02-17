package com.gmaur.investment.robotadvisor.domain

import java.math.BigDecimal

open class PortfolioRebalancer {
    open fun rebalance(ideal: AssetAllocation, current: Portfolio): Operations {
        if (ideal.matches(current)) {
            return Operations(listOf())
        }

        val amount = current.assets
                .filter { it is TransferrableAsset }
                .map { (it as TransferrableAsset).asset.amount }
                .foldRight(Amount(BigDecimal.ZERO), { a, b ->
                    a.add(b)
                })

        val isin = (current.assets.first() as Asset).isin
        return Operations(listOf(Purchase(Asset(isin, amount))))
    }
}