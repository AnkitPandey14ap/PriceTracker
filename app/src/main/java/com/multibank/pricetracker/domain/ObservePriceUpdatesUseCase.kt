package com.multibank.pricetracker.domain

import com.multibank.pricetracker.data.PriceUpdate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePriceUpdatesUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke(): Flow<PriceUpdate> {
        return repository.priceUpdates
    }
}