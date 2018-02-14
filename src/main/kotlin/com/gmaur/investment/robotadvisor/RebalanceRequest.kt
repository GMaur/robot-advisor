package com.gmaur.investment.robotadvisor

import com.gmaur.investment.robotadvisor.domain.Portfolio

data class RebalanceRequest(val ideal: Portfolio, val current: Portfolio)