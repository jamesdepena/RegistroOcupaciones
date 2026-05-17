package edu.ucne.registroocupaciones.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.registroocupaciones.data.db.OcupacionDatabase
import edu.ucne.registroocupaciones.data.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.repository.EmpleadoRepositoryImpl
import edu.ucne.registroocupaciones.data.repository.OcupacionRepositoryImpl
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import javax.inject.Singleton

@InstallIn(
    SingletonComponent::class
)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideOcupacionDb(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            OcupacionDatabase::class.java,
            "ocupacion_database"
        ).fallbackToDestructiveMigration()
            .build()


    @Provides
    @Singleton
    fun provideOcupacionDao(ocupacionDatabase: OcupacionDatabase): OcupacionDao {
        return ocupacionDatabase.ocupacionDao()
    }

    @Provides
    @Singleton
    fun provideOcupacionRepositoryImpl(ocupacionDao: OcupacionDao): OcupacionRepositoryImpl {
        return OcupacionRepositoryImpl(ocupacionDao)
    }

    @Provides
    @Singleton
    fun provideOcupacionRepository(impl: OcupacionRepositoryImpl): OcupacionRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideEmpleadoDao(ocupacionDatabase: OcupacionDatabase): EmpleadoDao {
        return ocupacionDatabase.empleadoDao()
    }

    @Provides
    @Singleton
    fun provideEmpleadoRepositoryImpl(empleadoDao: EmpleadoDao): EmpleadoRepositoryImpl {
        return EmpleadoRepositoryImpl(empleadoDao)
    }

    @Provides
    @Singleton
    fun provideEmpleadoRepository(impl: EmpleadoRepositoryImpl): EmpleadoRepository {
        return impl
    }
}