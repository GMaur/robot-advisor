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
                    val amount1 = AmountDTO.EUR(operation.amount().asString())
                    val (asset, amount) = when (operation.assetDefinition) {
                        is CashDefinition -> {
                            Pair(XCash(), amount1)
                        }
                        is FundDefinition -> {
                            Pair(XFund(operation.assetDefinition.id().value()), amount1)
                        }
                        else -> {
                            throw IllegalArgumentException()
                        }
                    }
                    OperationDTO(
                            type = operation.javaClass.simpleName.toLowerCase(),
                            asset = asset,
                            amount = amount
                    )
                }
        )
    }

    fun toDomain(dto: AssetAllocationDTO): Either<Exception, AssetAllocation> {
        return AssetAllocation.aNew(
                dto.assetAllocation.map { elementDTO ->
                    val percentage = elementDTO.percentage
                    val value = percentage.substring(0, percentage.length - 1)
                    val textOverOne = BigDecimal(value).divide(BigDecimal.valueOf(100L), MathContext(2, RoundingMode.HALF_EVEN)).toString()
                    AssetAllocationSingle(ISIN(elementDTO.isin), Percentage(textOverOne))
                })
    }

    fun toDomain(dto: PortfolioDTO): Portfolio<Asset> {
        return Portfolio(
                dto.assets.map { elementDTO ->
                    when (elementDTO) {
                        is FundDTO -> {
                            FundAsset(FundDefinition(isin = ISIN(elementDTO.isin)), amount = Amount(BigDecimal(elementDTO.price)))
                        }
                        is CashDTO -> {
                            Cash(amount = Amount(BigDecimal(elementDTO.value)))
                        }
                        else -> {
                            throw IllegalArgumentException()
                        }
                    }

                }
        )
    }
}