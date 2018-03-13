package com.gmaur.investment.robotadvisor.domain

class AssetObjectMother {
    companion object {
        fun cash(amountValue: Long): Asset {
            return cash(amountValue.toString())
        }

        fun cash(amountValue: String): Asset {
            return Cash(Amount.EUR(amountValue))
        }

        fun fund(isinValue: String, amountValue: String): Asset {
            return FundAsset(FundDefinition(ISIN(isinValue)), Amount.EUR(amountValue))
        }
    }

}