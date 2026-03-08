package com.multibank.pricetracker.domain.usecase

import com.multibank.pricetracker.domain.repository.PriceFeedRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ObservePriceFeedErrorsUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke(): SharedFlow<String> = repository.errorMessages
}
