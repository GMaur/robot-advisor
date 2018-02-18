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

        val dtos = OperationMapper().map(ops)

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
}