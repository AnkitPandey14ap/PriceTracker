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

class ObserveConnectionStateUseCaseTest {

    private val fakeRepository = FakePriceFeedRepository()
    private val useCase = ObserveConnectionStateUseCase(fakeRepository)

    @Test
    fun invoke_returnsConnectionStateFromRepository() {
        fakeRepository.setConnectionState(ConnectionStateEntity.Connected)

        val state = useCase().value

        assertEquals(ConnectionStateEntity.Connected, state)
    }

    @Test
    fun invoke_whenRepositoryDisconnected_returnsDisconnected() {
        fakeRepository.setConnectionState(ConnectionStateEntity.Disconnected)

        val state = useCase().value

        assertEquals(ConnectionStateEntity.Disconnected, state)
    }

    private class FakePriceFeedRepository : PriceFeedRepository {
        private val _connectionState = MutableStateFlow<ConnectionStateEntity>(ConnectionStateEntity.Disconnected)
        override val connectionState: StateFlow<ConnectionStateEntity> = _connectionState.asStateFlow()

        fun setConnectionState(state: ConnectionStateEntity) {
            _connectionState.value = state
        }

        override fun getInitialSymbols(): List<StockSymbolEntity> = emptyList()
        override val priceUpdates = flowOf<PriceUpdateEntity>()
        private val _errorMessages = MutableSharedFlow<String>()
        override val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()
        override fun start(symbols: List<StockSymbolEntity>) {}
        override fun stop() {}
    }
}
