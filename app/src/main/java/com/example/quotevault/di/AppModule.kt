package com.example.quotevault.di


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.quotevault.data.local.QuoteDatabase
import com.example.quotevault.data.local.dao.QuoteDao
import com.example.quotevault.data.model.UserPreferences
import com.example.quotevault.data.model.UserPreferencesRepository
import com.example.quotevault.quotes.QuoteRepository
import com.example.quotevault.quotes.QuoteRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /* ---------- ROOM ---------- */

    @Provides
    @Singleton
    fun provideQuoteDatabase(
        @ApplicationContext context: Context
    ): QuoteDatabase =
        QuoteDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideQuoteDao(
        database: QuoteDatabase
    ): QuoteDao =
        database.quoteDao()

    /* ---------- REPOSITORY ---------- */

    @Provides
    @Singleton
    fun provideQuoteRepository(): QuoteRepository {
        return QuoteRepositoryImpl()
    }

    /* ---------- DATASTORE ---------- */

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun provideUserPreferences(
        dataStore: DataStore<Preferences>
    ): UserPreferences =
        UserPreferencesRepository(dataStore)
}