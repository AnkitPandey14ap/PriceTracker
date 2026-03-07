package com.multibank.pricetracker.domain

import com.multibank.pricetracker.ui.feed.ConnectionState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveConnectionStateUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke(): StateFlow<ConnectionState> {
        return repository.connectionState
    }
}