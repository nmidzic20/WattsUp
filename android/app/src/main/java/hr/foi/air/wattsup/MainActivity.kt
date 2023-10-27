package hr.foi.air.wattsup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.foi.air.wattsup.pages.ChargerPage
import hr.foi.air.wattsup.pages.LandingPage
import hr.foi.air.wattsup.ui.theme.WattsUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WattsUpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "landing") {
                        composable("landing") {
                            val onChargerModeClick = { navController.navigate("chargerMode") }

                            LandingPage(onChargerModeClick)
                        }
                        composable("chargerMode") { ChargerPage { navController.popBackStack() } }
                    }
                }
            }
        }
    }
}
