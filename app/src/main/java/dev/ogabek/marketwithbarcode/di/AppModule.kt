package dev.ogabek.marketwithbarcode.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ogabek.marketwithbarcode.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun appDatabase(context: Application) = AppDatabase.getAppDBInstance(context)

    @Provides
    @Singleton
    fun productsDao(appDatabase: AppDatabase) = appDatabase.getProductsDao()

}