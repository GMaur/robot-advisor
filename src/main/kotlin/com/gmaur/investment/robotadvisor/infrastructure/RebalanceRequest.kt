package com.gmaur.investment.robotadvisor.infrastructure

import com.gmaur.investment.robotadvisor.domain.AssetAllocation
import com.gmaur.investment.robotadvisor.domain.Portfolio

data class RebalanceRequest(val ideal: AssetAllocation, val current: Portfolio)