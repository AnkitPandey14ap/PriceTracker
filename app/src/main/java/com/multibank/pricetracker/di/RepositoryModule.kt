package com.multibank.pricetracker.di

import com.multibank.pricetracker.data.repoImpl.PriceFeedRepositoryImpl
import com.multibank.pricetracker.domain.PriceFeedRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPriceFeedRepository(
        impl: PriceFeedRepositoryImpl
    ): PriceFeedRepository
}