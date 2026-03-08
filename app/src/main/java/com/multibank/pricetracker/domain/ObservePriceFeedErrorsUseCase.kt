package com.multibank.pricetracker.domain

import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ObservePriceFeedErrorsUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke(): SharedFlow<String> = repository.errorMessages
}
