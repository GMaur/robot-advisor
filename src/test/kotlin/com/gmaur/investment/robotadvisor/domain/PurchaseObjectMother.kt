package com.gmaur.investment.robotadvisor.domain

import java.math.BigDecimal

class PurchaseObjectMother {
    companion object {
        fun fund(isin: String, value: String): Purchase {
            return Purchase(FundDefinition(ISIN(isin)), Amount(BigDecimal(value)))
        }
    }

}