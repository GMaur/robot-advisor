package com.gmaur.investment.robotadvisor.infrastructure

import com.gmaur.investment.robotadvisor.domain.Amount
import com.gmaur.investment.robotadvisor.domain.Asset
import com.gmaur.investment.robotadvisor.domain.ISIN
import com.gmaur.investment.robotadvisor.domain.Portfolio
import java.math.BigDecimal

class FilePortfolioRepository {
    fun read(): Portfolio {
        return Portfolio(listOf(
                Asset(ISIN("LU1"), Amount(BigDecimal("8")), false),
                Asset(ISIN("LU2"), Amount(BigDecimal("2")), false),
                Asset(ISIN("LU2"), Amount(BigDecimal("90")), true)
        ))
    }
}