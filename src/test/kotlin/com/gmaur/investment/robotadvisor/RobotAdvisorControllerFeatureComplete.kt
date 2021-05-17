package com.gmaur.investment.robotadvisor

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.gmaur.investment.robotadvisor.RobotAdvisorControllerFeatureComplete.RealPortfolioRebalancer
import com.gmaur.investment.robotadvisor.infrastructure.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [RobotAdvisorController::class, RealPortfolioRebalancer::class, AppConfiguration::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RobotAdvisorControllerFeatureComplete {
    @LocalServerPort
    var port: Int? = null

    private val objectMapper: ObjectMapper = JSONMapper.aNew()

    @BeforeEach
    fun setUp() {
        FuelManager.instance.basePath = "http://localhost:" + port!!
    }

    @Test
    fun `balances a portfolio comparing to the ideal distribution`() {
        val assetAllocation = AssetAllocationDTO(listOf(
                AssetAllocationElementDTO(isin = "LU1", percentage = "80%"),
                AssetAllocationElementDTO(isin = "LU2", percentage = "20%")))
        val currentPortfolio = PortfolioDTO(listOf(
                FundDTO(isin = "LU1", price = "8"),
                FundDTO(isin = "LU2", price = "2"),
                CashDTO(value = "90")))
//        val jsonPayload = Files.readAllLines(Paths.get("/tmp", "rebalance_request.json")).joinToString("")
        val jsonPayload = serialize(RebalanceRequest(ideal = assetAllocation, current = currentPortfolio))

        println(jsonPayload)

        val response: Either<Exception /* = java.lang.Exception */, Pair<Response, Result<String, FuelError>>> = balancePortfolio(jsonPayload)

        assertThat(response.isRight())
        response.bimap(
                {
                    fail<java.lang.Exception>("expected a right")
                },
                { (response, result) ->
                    assertThat(response.statusCode).isEqualTo(200)
                    when (result) {
                        is Result.Success -> {
                            println(result.value)
                            assertThat(deserialize(result.value)).isEqualTo(
                                    OperationsDTO(listOf(
                                            OperationDTO(type = "purchase", asset = XFund("LU1"), amount = AmountDTO.EUR("72.00")),
                                            OperationDTO(type = "purchase", asset = XFund("LU2"), amount = AmountDTO.EUR("18.00"))
                                    )))
                        }
                        else -> {
                            fail("expected a Result.success")
                        }
                    }
                })
    }

    @Test
    fun `contributes to a portfolio comparing to the ideal distribution`() {
        val assetAllocation = AssetAllocationDTO(listOf(
                fundDTO("LU1", "80%"),
                fundDTO("LU2", "20%")))
        val cash = CashDTO(value = "100")
        val jsonPayload = serialize(ContributeRequest(ideal = assetAllocation, cash = cash))

        println(jsonPayload)

        val response = contributeToPortfolio(jsonPayload)

        assertThat(response.isRight())
        response.bimap(
                {
                    fail<java.lang.Exception>("expected a right")
                },
                { (response, result) ->
                    assertThat(response.statusCode).isEqualTo(200)
                    when (result) {
                        is Result.Success -> {
                            println(result.value)
                            assertThat(deserialize(result.value)).isEqualTo(
                                    OperationsDTO(listOf(
                                            OperationDTO(type = "purchase", asset = XFund("LU1"), amount = AmountDTO.EUR("80.00")),
                                            OperationDTO(type = "purchase", asset = XFund("LU2"), amount = AmountDTO.EUR("20.00"))
                                    )))
                        }
                        else -> {
                            fail("expected a Result.success")
                        }
                    }
                })
    }

    private fun fundDTO(isin: String, percentage: String): AssetAllocationElementDTO {
        return AssetAllocationElementDTO(isin = isin, percentage = percentage)
    }

    private fun balancePortfolio(jsonPayload: String): Either<Exception, Pair<Response, Result<String, FuelError>>> {
        val request = post("/rebalance", jsonPayload)
        return processRequest(request)
    }

    private fun contributeToPortfolio(jsonPayload: String): Either<Exception, Pair<Response, Result<String, FuelError>>> {
        val request = post("/contribute", jsonPayload)
        return processRequest(request)
    }

    private fun post(url: String, jsonPayload: String) = url.httpPost().body(jsonPayload, Charsets.UTF_8).header("Content-Type" to "application/json")

    private fun processRequest(httpPost: Request): Either<Exception, Pair<Response, Result<String, FuelError>>> {
        try {
            val (_, response, result) = httpPost.responseString()
            return Either.right(Pair(response, result))
        } catch (e: Exception) {
            e.printStackTrace()
            return Either.left(e)
        }
    }

    private fun serialize(request: Any): String {
        return objectMapper.writeValueAsString(request)
    }


    private fun deserialize(get: String): OperationsDTO {
        return objectMapper.readValue<OperationsDTO>(get, OperationsDTO::class.java)
    }

    @Configuration
    class RealPortfolioRebalancer {
    }

}

