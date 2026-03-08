package com.multibank.pricetracker.di

import com.multibank.pricetracker.data.core.DefaultWebSocketEventInterceptor
import com.multibank.pricetracker.data.core.WebSocketEventInterceptor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun bindWebSocketEventInterceptor(
        impl: DefaultWebSocketEventInterceptor
    ): WebSocketEventInterceptor
}
