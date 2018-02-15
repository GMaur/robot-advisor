package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.Portfolio
import com.gmaur.investment.robotadvisor.infrastructure.AssetAllocation

data class RebalanceRequest(val ideal: AssetAllocation, val current: Portfolio)