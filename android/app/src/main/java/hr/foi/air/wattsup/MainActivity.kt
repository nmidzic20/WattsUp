package hr.foi.air.wattsup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.foi.air.wattsup.pages.ChargerPage
import hr.foi.air.wattsup.pages.LandingPage
import hr.foi.air.wattsup.pages.LoginPage
import hr.foi.air.wattsup.pages.RegistrationPage
import hr.foi.air.wattsup.pages.ScanRFIDPage
import hr.foi.air.wattsup.ui.theme.WattsUpTheme
import hr.foi.air.wattsup.viewmodels.ChargerViewModel

class MainActivity : ComponentActivity() {
    private val chargerViewModel: ChargerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WattsUpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    val onArrowBackClick = {
                        navController.popBackStack()
                        Unit
                    }

                    NavHost(navController = navController, startDestination = "landing") {
                        composable("landing") {
                            val onChargerModeClick = { navController.navigate("scanRFID") }
                            val onUserModeClick= { navController.navigate("login") }

                            LandingPage(onChargerModeClick,onUserModeClick)
                        }
                        composable("scanRFID") {
                            val onScanRFID = { navController.navigate("chargerMode") }

                            ScanRFIDPage(onArrowBackClick, onScanRFID)
                        }
                        composable("chargerMode") {
                            ChargerPage(onArrowBackClick, chargerViewModel)
                        }
                        composable("registration"){
                            val onLogInClick = {navController.navigate("login")}
                            RegistrationPage(onArrowBackClick, onLogInClick)
                        }
                        composable("login"){
                            val onRegisterClick = {navController.navigate("registration")}
                            LoginPage(onRegisterClick)
                        }
                    }
                }
            }
        }
    }
}
