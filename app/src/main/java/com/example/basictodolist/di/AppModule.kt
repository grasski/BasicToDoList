package com.example.basictodolist.di

import android.content.Context
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.Room
import com.example.basictodolist.Constants.DATABASE_NAME
import com.example.basictodolist.db.AppDatabase
import com.example.basictodolist.db.DatabaseDao
import com.example.basictodolist.repository.AppRepository
import com.example.basictodolist.ui.viewmodel.AppViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideDbDao(db: AppDatabase): DatabaseDao {
        return db.getDbDao()
    }

    @Provides
    @Singleton
    fun provideAppRepository(dbDao: DatabaseDao): AppRepository {
        return AppRepository(dbDao)
    }

    @Provides
    @Singleton
    fun provideAppViewModel(appRepository: AppRepository): AppViewModel{
        return AppViewModel(appRepository)
    }
}


