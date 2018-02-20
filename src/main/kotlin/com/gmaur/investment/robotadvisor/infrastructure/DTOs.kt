package com.gmaur.investment.robotadvisor.infrastructure

import com.fasterxml.jackson.annotation.JsonCreator

data class AssetAllocationDTO(val listOf: List<AssetAllocationElementDTO>)
data class PortfolioDTO(val assets: List<XDTO>)
data class XDTO(val asset: AssetDTO, val amount: AmountDTO) {

    companion object {
        @JsonCreator
        fun parse(x: String) {
            println(x)
        }
    }
}

data class OperationsDTO(val operations: List<OperationDTO>)
data class OperationDTO(val type: String, val asset: AssetDTO, val amount: AmountDTO)
data class AssetDTO(val isin: String, val transferrable: Boolean)
data class AmountDTO private constructor(val value: String, val currency: String) {
    companion object {
        fun EUR(value: String): AmountDTO {
            return AmountDTO(value, "EUR")
        }
    }
}

data class AssetAllocationElementDTO(val isin: String, val percentage: String)