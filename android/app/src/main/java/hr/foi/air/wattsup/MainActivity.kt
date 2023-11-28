package hr.foi.air.wattsup

import ScanViewModel
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.foi.air.wattsup.screens.ChargerScreen
import hr.foi.air.wattsup.screens.LandingScreen
import hr.foi.air.wattsup.screens.LoginScreen
import hr.foi.air.wattsup.screens.RegistrationScreen
import hr.foi.air.wattsup.screens.ScanScreen
import hr.foi.air.wattsup.ui.theme.WattsUpTheme
import hr.foi.air.wattsup.viewmodels.ChargerViewModel

class MainActivity : ComponentActivity() {

    private val REQUEST_PERMISSIONS = 1

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
    )

    private val chargerViewModel: ChargerViewModel by viewModels()
    private val scanViewModel: ScanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(this)
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
                            val onUserModeClick = { navController.navigate("login") }

                            LandingScreen(onChargerModeClick, onUserModeClick)
                        }
                        composable("scanRFID") {
                            val onScanRFID = { navController.navigate("chargerMode") }

                            ScanScreen(onArrowBackClick, onScanRFID, scanViewModel)
                        }
                        composable("chargerMode") {
                            ChargerScreen(onArrowBackClick, chargerViewModel)
                        }
                        composable("registration") {
                            val onLogInClick = { navController.navigate("login") }
                            RegistrationScreen(onArrowBackClick, onLogInClick)
                        }
                        composable("login") {
                            val onRegisterClick = { navController.navigate("registration") }
                            LoginScreen(onRegisterClick)
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissions(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsToRequest = REQUIRED_PERMISSIONS.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }
            if (permissionsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    permissionsToRequest.toTypedArray(),
                    REQUEST_PERMISSIONS,
                )
            }
        }
    }
}
