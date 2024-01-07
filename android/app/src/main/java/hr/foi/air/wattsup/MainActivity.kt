package hr.foi.air.wattsup

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.foi.air.wattsup.ble.BLEManager
import hr.foi.air.wattsup.core.CardManager
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.rfid.RFIDManager
import hr.foi.air.wattsup.screens.CardScreen
import hr.foi.air.wattsup.screens.ChargerScreen
import hr.foi.air.wattsup.screens.HistoryScreen
import hr.foi.air.wattsup.screens.LandingScreen
import hr.foi.air.wattsup.screens.LoginScreen
import hr.foi.air.wattsup.screens.RegistrationScreen
import hr.foi.air.wattsup.screens.ScanScreen
import hr.foi.air.wattsup.screens.SimulatorScreen
import hr.foi.air.wattsup.screens.UserModeScreen
import hr.foi.air.wattsup.ui.theme.WattsUpTheme
import hr.foi.air.wattsup.viewmodels.AuthenticationViewModel
import hr.foi.air.wattsup.viewmodels.CardViewModel
import hr.foi.air.wattsup.viewmodels.ChargerViewModel
import hr.foi.air.wattsup.viewmodels.HistoryViewModel
import hr.foi.air.wattsup.viewmodels.ScanViewModel

class MainActivity : ComponentActivity() {

    private val REQUEST_PERMISSIONS = 1
    private lateinit var REQUIRED_PERMISSIONS: List<String>

    private val chargerViewModel: ChargerViewModel by viewModels()
    private val scanViewModel: ScanViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val cardViewModel: CardViewModel by viewModels()
    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    private var cardManagers: List<CardManager> = emptyList()
    private var receivers: MutableList<BroadcastReceiver> =
        emptyList<BroadcastReceiver>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cardManagers = listOf(
            BLEManager(this),
            RFIDManager(this),
        )

        REQUIRED_PERMISSIONS = cardManagers.map { it.getRequiredPermissions() }.flatten()
        requestPermissions(this)

        cardManagers.forEach { cardManager ->
            val receiver = cardManager.getStateReceiver()
            val action = cardManager.getAction()
            registerCardSupportStateReceiver(receiver, action)
            receivers.add(receiver)
        }

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
                    val onLogOutClick = { navController.navigate("landing") }

                    NavHost(navController = navController, startDestination = "landing") {
                        composable("landing") {
                            val onChargerModeClick = { navController.navigate("scanCard") }
                            val onUserModeClick = {
                                val isLoggedIn =
                                    TokenManager.getInstance(this@MainActivity).isLoggedIn()

                                if (isLoggedIn) {
                                    navController.navigate("userMode")
                                } else {
                                    navController.navigate("login")
                                }
                            }

                            BackHandler(true) { }

                            LandingScreen(onChargerModeClick, onUserModeClick)
                        }
                        composable("scanCard") {
                            val onScan = { navController.navigate("chargerMode") }

                            ScanScreen(
                                stringResource(R.string.charger_mode),
                                false,
                                { navController.navigate("landing") },
                                onScan,
                                scanViewModel,
                                cardManagers,
                            )
                        }
                        composable("addCard") {
                            val onScan = {
                                navController.navigate(
                                    navController.previousBackStackEntry?.destination?.route!!,
                                ) {
                                    popUpTo(navController.previousBackStackEntry?.destination?.route!!) {
                                        inclusive = true
                                    }
                                }
                            }

                            ScanScreen(
                                stringResource(R.string.scan_card),
                                true,
                                onArrowBackClick,
                                onScan,
                                scanViewModel,
                                cardManagers,
                            )
                        }
                        composable("chargerMode") {
                            ChargerScreen({
                                navController.navigate("scanCard")
                            }, {
                                navController.navigate("EVsimulator")
                            }, chargerViewModel)
                        }
                        composable("registration") {
                            val onLogInClick = { navController.navigate("login") }
                            var onAddCard = { navController.navigate("addCard") }
                            val onArrowBackClick = { navController.navigate("landing") }

                            RegistrationScreen(
                                onArrowBackClick,
                                onLogInClick,
                                onAddCard,
                                authenticationViewModel,
                            )
                        }
                        composable("login") {
                            val onRegisterClick = { navController.navigate("registration") }
                            val onLogin = { navController.navigate("userMode") }
                            LoginScreen(
                                onRegisterClick,
                                onLogin,
                                onArrowBackClick,
                                authenticationViewModel,
                            )
                        }
                        composable("userMode") {
                            val onHistoryClick = { navController.navigate("chargingHistory") }
                            val onCardsClick = { navController.navigate("myCards") }
                            val onArrowBackClick = { navController.navigate("landing") }

                            BackHandler(true) { }
                            UserModeScreen(
                                onHistoryClick,
                                onCardsClick,
                                onArrowBackClick,
                            )
                        }
                        composable("chargingHistory") {
                            HistoryScreen(onArrowBackClick, onLogOutClick, historyViewModel)
                        }
                        composable("EVsimulator") {
                            SimulatorScreen(chargerViewModel) {
                                navController.navigate("chargerMode")
                            }
                        }
                        composable("myCards") {
                            val onAddCard = { navController.navigate("addCard") }
                            CardScreen(onArrowBackClick, onAddCard, onLogOutClick, cardViewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        receivers.forEach { receiver ->
            unregisterReceiver(receiver)
        }

        super.onDestroy()
    }

    private fun registerCardSupportStateReceiver(
        cardSupportStateReceiver: BroadcastReceiver,
        action: String,
    ) {
        val filter = IntentFilter(action)
        this.registerReceiver(cardSupportStateReceiver, filter)
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
