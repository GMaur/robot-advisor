package com.gmaur.investment.robotadvisor.infrastructure

import com.gmaur.investment.robotadvisor.domain.Operations

data class OperationsDTO(val operations: List<OperationDTO>)
data class OperationDTO(val type: String, val asset: AssetDTO, val amount: AmountDTO)
data class AssetDTO(val isin: String)
data class AmountDTO(val value: String, val currency: String)

class OperationMapper {
    fun map(it: Operations): OperationsDTO {
        return OperationsDTO(
                it.operations.map { operation ->
                    OperationDTO(
                            type = operation.javaClass.simpleName,
                            asset = AssetDTO(isin = operation.asset.isin.value),
                            amount = AmountDTO(value = operation.asset.amount.asString(), currency = "EUR")
                    )
                }
        )
    }
}