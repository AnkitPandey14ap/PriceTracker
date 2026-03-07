package com.multibank.pricetracker.domain

import javax.inject.Inject

class StopFeedUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke() {
        repository.stop()
    }
}