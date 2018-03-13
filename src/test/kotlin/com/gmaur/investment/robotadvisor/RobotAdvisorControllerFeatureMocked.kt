package com.gmaur.investment.robotadvisor

import arrow.core.Either
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.gmaur.investment.robotadvisor.RobotAdvisorControllerFeatureMocked.FakeConfiguration
import com.gmaur.investment.robotadvisor.domain.PortfolioRebalancer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@Import(value = [RobotAdvisorController::class, FakeConfiguration::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RobotAdvisorControllerFeatureMocked {
    @LocalServerPort
    var port: Int? = null

    @Autowired
    private lateinit var portfolioRebalancer: PortfolioRebalancer

    @Before
    fun setUp() {
        FuelManager.instance.basePath = "http://localhost:" + port!!
    }

    @Test
    fun `incomplete request`() {
        var jsonPayload = """
            {"ideal":{"values":[{"isin":{"value":"LU1"},"percentage":{"value":"80"}},{"isin":{"value":"LU2"},"percentage":{"value":"20"}}]},
            "current":null}
            """ // missing the current portfolio

        val result = balancePortfolio(jsonPayload)


        assertThat(result.isRight()).isTrue()
        result.bimap(
                { fail("should be a right") },
                { (response, result) ->
                    assertThat(response.statusCode).isEqualTo(400)
                    when (result) {
                        is Result.Success -> {
                            fail("should be a failure")
                        }
                    }
                })
        Mockito.verifyZeroInteractions(portfolioRebalancer)
    }

    private fun balancePortfolio(jsonPayload: String): Either<Exception, Pair<Response, Result<String, FuelError>>> {
        val httpPost = "/rebalance/".httpPost().body(jsonPayload, Charsets.UTF_8).header("Content-Type" to "application/json")
        try {
            val (_, response, result) = httpPost.responseString()
            return Either.right(Pair(response, result))
        } catch (e: Exception) {
            e.printStackTrace()
            return Either.left(e)
        }

    }

    @Configuration
    class FakeConfiguration {
        private val portfolioRebalancer = Mockito.mock(PortfolioRebalancer::class.java)

        @Bean
        fun rebalancer(): PortfolioRebalancer {
            return portfolioRebalancer
        }
    }
}