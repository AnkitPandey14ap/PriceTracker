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

class ObservePriceFeedErrorsUseCaseTest {

    private val fakeRepository = FakePriceFeedRepository()
    private val useCase = ObservePriceFeedErrorsUseCase(fakeRepository)

    @Test
    fun invoke_returnsErrorMessagesFromRepository() = runTest {
        fakeRepository.emitError("Network error")

        val messages = useCase().take(1).toList()

        assertEquals(listOf("Network error"), messages)
    }

    private class FakePriceFeedRepository : PriceFeedRepository {
        private val _errorMessages = MutableSharedFlow<String>(replay = 10)

        fun emitError(message: String) {
            _errorMessages.tryEmit(message)
        }

        override fun getInitialSymbols(): List<StockSymbolEntity> = emptyList()
        override val priceUpdates = flowOf<PriceUpdateEntity>()
        override val connectionState: StateFlow<ConnectionStateEntity> =
            MutableStateFlow(ConnectionStateEntity.Disconnected).asStateFlow()
        override val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()
        override fun start(symbols: List<StockSymbolEntity>) {}
        override fun stop() {}
    }
}
