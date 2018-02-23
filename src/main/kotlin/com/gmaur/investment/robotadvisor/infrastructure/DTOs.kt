package com.gmaur.investment.robotadvisor.infrastructure

data class AssetAllocationDTO(val assetAllocation: List<AssetAllocationElementDTO>)
data class PortfolioDTO(val assets: List<AssetDTO>)

//This is output from our robot-advisor
data class OperationsDTO(val operations: List<OperationDTO>)

data class OperationDTO(val type: String, val asset: X, val amount: AmountDTO)

interface X
data class XFund(val isin: String) : X {
    public val type = "fund"
}

class XCash : X {
    public val type = "cash"
}

interface AssetDTO
data class FundDTO(val isin: String, val price: String) : AssetDTO {
    public val type = "fund"
}

data class CashDTO(val value: String) : AssetDTO {
    public val type = "cash"
}

data class AmountDTO private constructor(val value: String, val currency: String) {
    companion object {
        fun EUR(value: String): AmountDTO {
            return AmountDTO(value, "EUR")
        }
    }
}

data class AssetAllocationElementDTO(val isin: String, val percentage: String)