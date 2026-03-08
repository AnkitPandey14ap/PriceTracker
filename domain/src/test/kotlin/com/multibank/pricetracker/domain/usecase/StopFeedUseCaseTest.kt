package com.multibank.pricetracker.domain.usecase

import com.multibank.pricetracker.domain.model.ConnectionStateEntity
import com.multibank.pricetracker.domain.model.PriceUpdateEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import com.multibank.pricetracker.domain.repository.PriceFeedRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

class StopFeedUseCaseTest {

    private val fakeRepository = FakePriceFeedRepository()
    private val useCase = StopFeedUseCase(fakeRepository)

    @Test
    fun invoke_callsRepositoryStop() {
        useCase()

        assertEquals(1, fakeRepository.stopCallCount)
    }

    @Test
    fun invoke_multipleTimes_incrementsStopCallCount() {
        useCase()
        useCase()

        assertEquals(2, fakeRepository.stopCallCount)
    }

    private class FakePriceFeedRepository : PriceFeedRepository {
        var stopCallCount = 0

        override fun getInitialSymbols(): List<StockSymbolEntity> = emptyList()
        override val priceUpdates = flowOf<PriceUpdateEntity>()
        override val connectionState: StateFlow<ConnectionStateEntity> =
            MutableStateFlow(ConnectionStateEntity.Disconnected).asStateFlow()
        private val _errorMessages = MutableSharedFlow<String>()
        override val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()
        override fun start(symbols: List<StockSymbolEntity>) {}
        override fun stop() { stopCallCount++ }
    }
}
