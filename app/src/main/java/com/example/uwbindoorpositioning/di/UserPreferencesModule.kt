package com.example.uwbindoorpositioning.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.uwbindoorpositioning.data.DefaultUserPreferencesRepository
import com.example.uwbindoorpositioning.data.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

/*
 * This module tells Hilt how to provide instances of UserPreferencesRepository
 * and DataStore<Preferences>
 */
@InstallIn(SingletonComponent::class)
@Module
abstract class UserPreferencesModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        defaultUserPreferencesRepository: DefaultUserPreferencesRepository
    ): UserPreferencesRepository

    companion object {
        @Provides
        @Singleton
        fun provideUserDataStorePreferences(
            @ApplicationContext applicationContext: Context
        ): DataStore<Preferences> {
            return applicationContext.dataStore
        }
    }
}