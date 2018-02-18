package com.gmaur.investment.robotadvisor.infrastructure

data class RebalanceRequest(val ideal: AssetAllocationDTO?, val current: PortfolioDTO?)