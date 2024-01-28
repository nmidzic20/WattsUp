package hr.foi.air.wattsup

import android.app.Application
import android.util.Log
import hr.foi.air.wattsup.di.dataModule
import hr.foi.air.wattsup.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WattsUpApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WattsUpApp)
            modules(
                dataModule,
                viewModelsModule,
            )
        }

        Log.d("WattsUp", "App started")
    }
}
