package com.gmaur.investment.robotadvisor.domain

class PurchaseObjectMother {
    companion object {
        fun fund(isin: String, value: String): Purchase {
            return Purchase(FundDefinition(ISIN(isin)), Amount.EUR(value))
        }
    }

}