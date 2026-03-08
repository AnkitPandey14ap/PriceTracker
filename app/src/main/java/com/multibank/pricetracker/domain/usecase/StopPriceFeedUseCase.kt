package com.multibank.pricetracker.domain.usecase

import com.multibank.pricetracker.domain.repository.PriceFeedRepository
import javax.inject.Inject

class StopFeedUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke() {
        repository.stop()
    }
}