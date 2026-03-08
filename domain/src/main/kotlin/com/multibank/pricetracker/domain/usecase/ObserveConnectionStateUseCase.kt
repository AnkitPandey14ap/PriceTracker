package com.multibank.pricetracker.domain.usecase

import com.multibank.pricetracker.domain.repository.PriceFeedRepository
import com.multibank.pricetracker.domain.model.ConnectionStateEntity
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveConnectionStateUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke(): StateFlow<ConnectionStateEntity> {
        return repository.connectionState
    }
}
