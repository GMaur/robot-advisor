package com.gmaur.investment.robotadvisor.infrastructure

import com.gmaur.investment.robotadvisor.domain.*
import org.assertj.core.api.SoftAssertions
import org.junit.Test
import java.math.BigDecimal

class OperationMapperShould {
    @Test
    fun `convert from domain to DTO`() {
        val ops = Operations(listOf(
                Purchase(Asset(ISIN("LU0"), Amount(BigDecimal.valueOf(0L)), false)),
                Purchase(Asset(ISIN("LU1"), Amount(BigDecimal.valueOf(1L)), true))
        ))

        val dtos = OperationMapper().toDTO(ops)

        var softly = SoftAssertions()

        softly.assertThat(dtos.operations).hasSize(2)
        softly.assertThat(dtos.operations[0].asset.isin).isEqualTo("LU0")
        softly.assertThat(dtos.operations[0].amount.value).isEqualTo("0.00")
        softly.assertThat(dtos.operations[0].amount.currency).isEqualTo("EUR")
        softly.assertThat(dtos.operations[0].type).isEqualTo("Purchase")

        softly.assertThat(dtos.operations[1].asset.isin).isEqualTo("LU1")
        softly.assertThat(dtos.operations[1].amount.value).isEqualTo("1.00")
        softly.assertThat(dtos.operations[1].amount.currency).isEqualTo("EUR")
        softly.assertThat(dtos.operations[1].type).isEqualTo("Purchase")

        softly.assertAll()
    }

    @Test
    fun `assetAllocationElementDTO - convert to domain`() {
        val assetAllocationElementDTO =
                AssetAllocationDTO(listOf(AssetAllocationElementDTO(isin = "LU1", percentage = "21%")))

        var eitherDomain = OperationMapper().toDomain(assetAllocationElementDTO)

        var softly = SoftAssertions()
        softly.assertThat(eitherDomain.isRight())
        val domain = eitherDomain.get()
        softly.assertThat(domain.values).hasSize(1)
        softly.assertThat(domain.values[0].isin).isEqualTo(ISIN("LU1"))
        softly.assertThat(domain.values[0].percentage).isEqualTo(Percentage("0.21"))

        softly.assertAll()
    }

    @Test
    fun `portfolioDTO - convert to domain`() {
        val portfolioDTO = PortfolioDTO(listOf(
                XDTO(AssetDTO(isin = "LU1", transferrable = true), amount = AmountDTO.EUR("100"))))

        val domain = OperationMapper().toDomain(portfolioDTO)

        var softly = SoftAssertions()

        softly.assertThat(domain.assets).hasSize(1)
        softly.assertThat(domain.assets[0].isin).isEqualTo(ISIN("LU1"))
        softly.assertThat(domain.assets[0].transferrable).isEqualTo(true)
        softly.assertThat(domain.assets[0].amount).isEqualTo(Amount(BigDecimal.valueOf(100L)))

        softly.assertAll()
    }
}