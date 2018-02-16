package com.gmaur.investment.robotadvisor.infrastructure

import com.gmaur.investment.robotadvisor.domain.*
import java.math.BigDecimal

class FilePortfolioRepository {
    fun read(): Portfolio {
        return Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal("8"))),
                Asset(ISIN("LU2"), Amount(BigDecimal("2"))),
                TransferrableAsset(Asset(ISIN("LU2"), Amount(BigDecimal("90"))))
        ))
    }
}