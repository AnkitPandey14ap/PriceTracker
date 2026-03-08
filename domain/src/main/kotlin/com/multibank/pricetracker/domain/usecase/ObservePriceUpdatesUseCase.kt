package com.multibank.pricetracker.domain.usecase

import com.multibank.pricetracker.domain.repository.PriceFeedRepository
import com.multibank.pricetracker.domain.model.PriceUpdateEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePriceUpdatesUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke(): Flow<PriceUpdateEntity> {
        return repository.priceUpdates
    }
}
