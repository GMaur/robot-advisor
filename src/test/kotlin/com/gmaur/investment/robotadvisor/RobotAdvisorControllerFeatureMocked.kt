package com.gmaur.investment.robotadvisor

import arrow.core.Either
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.gmaur.investment.robotadvisor.RobotAdvisorControllerFeatureMocked.FakeConfiguration
import com.gmaur.investment.robotadvisor.domain.ContributeStrategy
import com.gmaur.investment.robotadvisor.domain.FixedContributeStrategy
import com.gmaur.investment.robotadvisor.domain.FixedRebalanceStrategy
import com.gmaur.investment.robotadvisor.domain.RebalancingStrategy
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.mockito.Mockito
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@ExtendWith(SpringExtension::class)
@Import(value = [RobotAdvisorController::class, FakeConfiguration::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RobotAdvisorControllerFeatureMocked {
    @LocalServerPort
    var port: Int? = null

    @Autowired
    private lateinit var contributeStrategy: ContributeStrategy

    @BeforeEach
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


        assertThat(result.isRight()).isTrue
        result.bimap(
                { fail<Exception>("should be a right") },
                { (response, result) ->
                    assertThat(response.statusCode).isEqualTo(400)
                    when (result) {
                        is Result.Success -> {
                            fail<Exception>("should be a failure")
                        }
                        else -> true
                    }
                })
        Mockito.verifyZeroInteractions(contributeStrategy)
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
        val contributeStrategy = Mockito.mock(FixedContributeStrategy::class.java)
        val rebalanceStrategy = Mockito.mock(FixedRebalanceStrategy::class.java)

        @Bean
        fun ContributeStrategy(): ContributeStrategy {
            return contributeStrategy
        }

        @Bean
        fun RebalancingStrategy(): RebalancingStrategy {
            return rebalanceStrategy
        }
    }
}
