package com.gmaur.investment.robotadvisor.infrastructure

import com.gmaur.investment.robotadvisor.domain.*
import org.assertj.core.api.SoftAssertions
import org.junit.Test
import java.math.BigDecimal


class DomainObjectMapperShould {

    private val mapper = JSONMapper.aNew()

    @Test
    fun `convert from domain to DTO`() {
        val ops = Operations(listOf(
                PurchaseObjectMother.fund("LU0", "0.00"),
                PurchaseObjectMother.fund("LU1", "1.00")
        ))

        val dtos = DomainObjectMapper().toDTO(ops)

        val softly = SoftAssertions()

        softly.assertThat(dtos.operations).hasSize(2)
        softly.assertThat((dtos.operations[0].asset as XFund).isin).isEqualTo("LU0")
        softly.assertThat(dtos.operations[0].amount.value).isEqualTo("0.00")
        softly.assertThat(dtos.operations[0].amount.currency).isEqualTo("EUR")
        softly.assertThat(dtos.operations[0].type).isEqualTo("purchase")

        softly.assertThat((dtos.operations[1].asset as XFund).isin).isEqualTo("LU1")
        softly.assertThat(dtos.operations[1].amount.value).isEqualTo("1.00")
        softly.assertThat(dtos.operations[1].amount.currency).isEqualTo("EUR")
        softly.assertThat(dtos.operations[1].type).isEqualTo("purchase")

        softly.assertAll()

        println(mapper.writeValueAsString(dtos))
    }

    @Test
    fun `generate sample assetallocation`() {
        val assetAllocation = AssetAllocationDTO(listOf(
                AssetAllocationElementDTO("LU21", "51%"),
                AssetAllocationElementDTO("LU22", "49%")))

        println(mapper.writeValueAsString(assetAllocation))
    }

    @Test
    fun `assetAllocationElementDTO - convert to domain`() {
        val assetAllocationElementDTO =
                AssetAllocationDTO(listOf(AssetAllocationElementDTO(isin = "LU1", percentage = "21%")))

        val eitherDomain = DomainObjectMapper().toDomain(assetAllocationElementDTO)

        val softly = SoftAssertions()
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
                FundDTO(isin = "LU1", price = "100")))

        val domain = DomainObjectMapper().toDomain(portfolioDTO)

        val softly = SoftAssertions()

        softly.assertThat(domain.assets).hasSize(1)
        val firstAsset = domain.assets[0] as FundAsset
        softly.assertThat(firstAsset.fund.id()).isEqualTo(ISIN("LU1"))
        softly.assertThat(firstAsset.amount()).isEqualTo(Amount(BigDecimal.valueOf(100L)))

        softly.assertAll()
    }
}