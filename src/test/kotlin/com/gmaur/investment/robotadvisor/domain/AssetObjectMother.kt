package com.gmaur.investment.robotadvisor.domain

class AssetObjectMother {
    companion object {
        fun cash(amountValue: String): Asset {
            return Cash(amount(amountValue))
        }

        fun fund(isinValue: String, amountValue: String): Asset {
            return FundAsset(FundDefinition(ISIN(isinValue)), amount(amountValue))
        }

        private fun amount(amountValue: String) = Amount.EUR(amountValue)
    }

}