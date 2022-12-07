package com.pmsi.lamaruin.di

import android.app.Application
import com.pmsi.lamaruin.data.remote.FirestoreService
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.Module
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideFirestoreModule() = FirestoreService()

}