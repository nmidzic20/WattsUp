package hr.foi.air.wattsup.di

import hr.foi.air.wattsup.viewmodels.AuthenticationViewModel
import hr.foi.air.wattsup.viewmodels.CardViewModel
import hr.foi.air.wattsup.viewmodels.HistoryViewModel
import hr.foi.air.wattsup.viewmodels.ScanViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel {
        AuthenticationViewModel(
            repository = get(),
        )
    }
    viewModel {
        CardViewModel(
            repository = get(),
        )
    }
    viewModel {
        HistoryViewModel(
            repository = get(),
        )
    }
    viewModel {
        ScanViewModel(
            repository = get(),
        )
    }
}
