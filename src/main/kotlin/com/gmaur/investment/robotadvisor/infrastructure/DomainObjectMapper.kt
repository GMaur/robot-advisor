package com.gmaur.investment.robotadvisor.infrastructure

import arrow.core.Either
import com.gmaur.investment.robotadvisor.domain.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class DomainObjectMapper {
    fun toDTO(it: Operations): OperationsDTO {
        return OperationsDTO(
                it.operations.map { operation ->
                    OperationDTO(
                            type = operation.javaClass.simpleName,
                            asset = AssetDTO(isin = operation.asset.isin.value, transferrable = false),
                            amount = AmountDTO.EUR(operation.asset.amount.asString())
                    )
                }
        )
    }

    fun toDomain(dto: AssetAllocationDTO): Either<Exception, AssetAllocation> {
        return AssetAllocation.aNew(
                dto.listOf.map { elementDTO ->
                    val percentage = elementDTO.percentage
                    val value = percentage.substring(0, percentage.length - 1)
                    val textOverOne = BigDecimal(value).divide(BigDecimal.valueOf(100L), MathContext(2, RoundingMode.HALF_EVEN)).toString()
                    AssetAllocationSingle(ISIN(elementDTO.isin), Percentage(textOverOne))
                })
    }

    fun toDomain(dto: PortfolioDTO): Portfolio {
        return Portfolio(
                dto.values.map { elementDTO ->
                    Asset(isin = ISIN(elementDTO.asset.isin), amount = Amount(BigDecimal(elementDTO.amount.value)), transferrable = elementDTO.asset.transferrable)
                }
        )
    }
}