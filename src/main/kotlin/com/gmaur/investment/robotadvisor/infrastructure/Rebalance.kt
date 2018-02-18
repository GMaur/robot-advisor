package com.gmaur.investment.robotadvisor.infrastructure

import arrow.core.Either
import com.gmaur.investment.robotadvisor.domain.AssetAllocation
import com.gmaur.investment.robotadvisor.domain.Portfolio

data class Rebalance(val current: Portfolio, val ideal: AssetAllocation) {

    companion object {
        private val domainObjectMapper: DomainObjectMapper = DomainObjectMapper()

        fun parse(rebalanceRequest: RebalanceRequest): Either<List<Error>, Rebalance> {
            if (rebalanceRequest.current != null && rebalanceRequest.ideal != null) {
                return Either.Right(Rebalance(current = domainObjectMapper.toDomain(rebalanceRequest.current), ideal = domainObjectMapper.toDomain(rebalanceRequest.ideal).get()))
            }
            return Either.Left(listOf(Error("null values")))
        }
    }

}