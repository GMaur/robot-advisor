package com.gmaur.investment.robotadvisor.infrastructure

data class AssetAllocationElementDTO(val isin: String, val percentage: String)
data class AssetAllocationDTO(val listOf: List<AssetAllocationElementDTO>)

data class PortfolioDTO(val values: List<XDTO>)
data class XDTO(val asset: AssetDTO, val amount: AmountDTO)