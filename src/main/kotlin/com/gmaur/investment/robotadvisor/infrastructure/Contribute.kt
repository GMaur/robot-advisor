package com.gmaur.investment.robotadvisor.infrastructure

import arrow.core.Either
import com.gmaur.investment.robotadvisor.domain.AssetAllocation
import com.gmaur.investment.robotadvisor.domain.Cash

data class ContributeRequest(val ideal: AssetAllocationDTO?, val cash: CashDTO?)

data class Contribute(val ideal: AssetAllocation, val cash: Cash) {
    companion object {

        private val domainObjectMapper: DomainObjectMapper = DomainObjectMapper()
        fun parse(requestDTO: ContributeRequest): Either<List<Error>, Contribute> {
            if (requestDTO.cash != null && requestDTO.ideal != null) {
                return domainObjectMapper
                        .toDomain(requestDTO.ideal)
                        .map { Contribute(it, domainObjectMapper.toDomain(requestDTO.cash)) }
                        .mapLeft { listOf(Error(it.message)) }
            }
            return Either.Left(listOf(Error("null values")))
        }

    }

}
