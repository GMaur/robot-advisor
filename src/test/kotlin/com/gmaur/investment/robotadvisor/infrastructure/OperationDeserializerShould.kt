package com.gmaur.investment.robotadvisor.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.Test


class OperationDeserializerShould {
    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    @Test
    fun `deserialize some purchase orders`() {
        val json = """
            {"operations":[
                {"type":"Purchase",
                 "asset":{
                    "isin": "LU1"},
                 "amount":{
                    "value":"21",
                    "currency":"EUR"}},
                {"type":"Purchase",
                 "asset":{
                    "isin": "LU2"},
                 "amount":{
                    "value":"22",
                    "currency":"USD"}}
                 ]}
            """
        val operations = objectMapper.readValue<OperationsDTO>(json, OperationsDTO::class.java)

        val softly = SoftAssertions()
        assertThat(operations.operations).hasSize(2)
        assertThat(operations.operations[0].type).isEqualTo("Purchase")
        assertThat(operations.operations[0].asset.isin).isEqualTo("LU1")
        assertThat(operations.operations[0].amount.value).isEqualTo("21")
        assertThat(operations.operations[0].amount.currency).isEqualTo("EUR")

        assertThat(operations.operations[1].type).isEqualTo("Purchase")
        assertThat(operations.operations[1].asset.isin).isEqualTo("LU2")
        assertThat(operations.operations[1].amount.value).isEqualTo("22")
        assertThat(operations.operations[1].amount.currency).isEqualTo("USD")
        softly.assertAll()
    }

}

