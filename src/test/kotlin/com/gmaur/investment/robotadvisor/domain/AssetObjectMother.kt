package com.gmaur.investment.robotadvisor.domain

import java.math.BigDecimal

class AssetObjectMother {
    companion object {
        fun cash(amountValue: Long): Asset {
            return cash(amountValue.toString())
        }

        fun cash(amountValue: String): Asset {
            return Cash(Amount(BigDecimal(amountValue)))
        }

        fun fund(isinValue: String, amountValue: Long): Asset {
            return fund(isinValue, amountValue.toString())
        }

        fun fund(isinValue: String, amountValue: String): Asset {
            return FundAsset(FundDefinition(ISIN(isinValue)), Amount(BigDecimal(amountValue)))
        }
    }

}