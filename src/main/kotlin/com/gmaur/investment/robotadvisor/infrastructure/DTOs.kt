package com.gmaur.investment.robotadvisor.infrastructure

data class AssetAllocationDTO(val listOf: List<AssetAllocationElementDTO>)
data class PortfolioDTO(val assets: List<AssetDTO>)

//This is output from our robot-advisor
data class OperationsDTO(val operations: List<OperationDTO>)

data class OperationDTO(val type: String, val asset: X, val amount: AmountDTO)

interface X
data class XFund(val isin: String) : X {
    public val type = "fund"
}

data class XCash(val none: String?) : X {
    public val type = "cash"
}

interface AssetIdDTO {
    fun value(): String?
}

data class FundIdDTO(val isin: String) : AssetIdDTO {
    override fun value(): String? {
        return isin
    }
}

data class NullAssetIdDTO(val id: String? = null) : AssetIdDTO {
    override fun value(): String? {
        return id
    }
}

interface AssetDTO {
    fun id(): AssetIdDTO
}

data class FundDTO(val isin: String, val amount: AmountDTO) : AssetDTO {
    override fun id(): AssetIdDTO {
        return FundIdDTO(isin)
    }
}

data class CashDTO(val amount: AmountDTO) : AssetDTO {
    override fun id(): AssetIdDTO {
        return NullAssetIdDTO()
    }
}

data class AmountDTO private constructor(val value: String, val currency: String) {
    companion object {
        fun EUR(value: String): AmountDTO {
            return AmountDTO(value, "EUR")
        }
    }
}

data class AssetAllocationElementDTO(val isin: String, val percentage: String)