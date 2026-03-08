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

class StartFeedUseCaseTest {

    private val fakeRepository = FakePriceFeedRepository()
    private val useCase = StartFeedUseCase(fakeRepository)

    @Test
    fun invoke_callsRepositoryStartWithSymbols() {
        val symbols = listOf(
            StockSymbolEntity("NVDA", 500.0, 498.0, description = "NVIDIA")
        )

        useCase(symbols)

        assertEquals(1, fakeRepository.startCallCount)
        assertEquals(symbols, fakeRepository.lastStartSymbols)
    }

    private class FakePriceFeedRepository : PriceFeedRepository {
        var startCallCount = 0
        var lastStartSymbols: List<StockSymbolEntity>? = null

        override fun getInitialSymbols(): List<StockSymbolEntity> = emptyList()
        override val priceUpdates = flowOf<PriceUpdateEntity>()
        override val connectionState: StateFlow<ConnectionStateEntity> =
            MutableStateFlow(ConnectionStateEntity.Disconnected).asStateFlow()
        private val _errorMessages = MutableSharedFlow<String>()
        override val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

        override fun start(symbols: List<StockSymbolEntity>) {
            startCallCount++
            lastStartSymbols = symbols
        }

        override fun stop() {}
    }
}
