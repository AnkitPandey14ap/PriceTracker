package com.multibank.pricetracker.di

import com.multibank.pricetracker.data.datasource.InitialSymbolsSource
import com.multibank.pricetracker.data.datasource.MockInitialSymbolsSource
import com.multibank.pricetracker.data.datasource.PriceFeedDataSource
import com.multibank.pricetracker.data.datasource.PriceMessageParser
import com.multibank.pricetracker.data.datasource.websocket.DefaultPriceMessageParser
import com.multibank.pricetracker.data.datasource.websocket.WebSocketService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindInitialSymbolsSource(
        impl: MockInitialSymbolsSource
    ): InitialSymbolsSource

    @Binds
    @Singleton
    abstract fun bindPriceFeedDataSource(
        impl: WebSocketService
    ): PriceFeedDataSource

    @Binds
    @Singleton
    abstract fun bindPriceMessageParser(
        impl: DefaultPriceMessageParser
    ): PriceMessageParser
}
