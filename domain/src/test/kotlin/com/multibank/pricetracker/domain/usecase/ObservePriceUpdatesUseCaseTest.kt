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
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservePriceUpdatesUseCaseTest {

    private val fakeRepository = FakePriceFeedRepository()
    private val useCase = ObservePriceUpdatesUseCase(fakeRepository)

    @Test
    fun invoke_emitsPriceUpdatesFromRepository() = runTest {
        val updates = listOf(
            PriceUpdateEntity("AAPL", 150.0),
            PriceUpdateEntity("GOOG", 140.0)
        )
        fakeRepository.emitPriceUpdates(updates)

        val collected = useCase().take(2).toList()

        assertEquals(updates, collected)
    }

    private class FakePriceFeedRepository : PriceFeedRepository {
        private val _priceUpdates = MutableSharedFlow<PriceUpdateEntity>(replay = 10)

        fun emitPriceUpdates(updates: List<PriceUpdateEntity>) {
            updates.forEach { _priceUpdates.tryEmit(it) }
        }

        override fun getInitialSymbols(): List<StockSymbolEntity> = emptyList()
        override val priceUpdates = _priceUpdates
        override val connectionState: StateFlow<ConnectionStateEntity> =
            MutableStateFlow(ConnectionStateEntity.Disconnected).asStateFlow()
        private val _errorMessages = MutableSharedFlow<String>()
        override val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()
        override fun start(symbols: List<StockSymbolEntity>) {}
        override fun stop() {}
    }
}
