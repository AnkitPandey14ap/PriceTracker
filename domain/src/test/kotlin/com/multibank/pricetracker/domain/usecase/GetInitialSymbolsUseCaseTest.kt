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

class GetInitialSymbolsUseCaseTest {

    private val fakeRepository = FakePriceFeedRepository()
    private val useCase = GetInitialSymbolsUseCase(fakeRepository)

    @Test
    fun invoke_returnsSymbolsFromRepository() {
        val expected = listOf(
            StockSymbolEntity("AAPL", 150.0, 148.0, description = "Apple"),
            StockSymbolEntity("GOOG", 140.0, 138.0, description = "Alphabet")
        )
        fakeRepository.symbolsToReturn = expected

        val result = useCase()

        assertEquals(expected, result)
    }

    @Test
    fun invoke_emptyRepository_returnsEmptyList() {
        fakeRepository.symbolsToReturn = emptyList()

        val result = useCase()

        assertEquals(emptyList<StockSymbolEntity>(), result)
    }

    private class FakePriceFeedRepository : PriceFeedRepository {
        var symbolsToReturn: List<StockSymbolEntity> = emptyList()

        override fun getInitialSymbols(): List<StockSymbolEntity> = symbolsToReturn

        override val priceUpdates = flowOf<PriceUpdateEntity>()
        override val connectionState: StateFlow<ConnectionStateEntity> =
            MutableStateFlow(ConnectionStateEntity.Disconnected).asStateFlow()
        private val _errorMessages = MutableSharedFlow<String>()
        override val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()
        override fun start(symbols: List<StockSymbolEntity>) {}
        override fun stop() {}
    }
}
