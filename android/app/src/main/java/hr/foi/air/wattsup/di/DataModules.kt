package hr.foi.air.wattsup.di

import hr.foi.air.wattsup.repository.WattsUpRepository
import hr.foi.air.wattsup.repository.WattsUpRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single<WattsUpRepository> { WattsUpRepositoryImpl() }
}
