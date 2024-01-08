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
import hr.foi.air.wattsup.utils.LastAddedCard
import hr.foi.air.wattsup.utils.LastRegisteredCard
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
                    val navigate = { route: String ->
                        navController.navigate(route)
                    }
                    val onLogOut = { navController.navigate(getString(R.string.landing_route)) }

                    NavHost(
                        navController = navController,
                        startDestination = getString(R.string.landing_route),
                    ) {
                        composable(getString(R.string.landing_route)) {
                            val onChargerModeClick =
                                { navigate(getString(R.string.scan_card_route)) }
                            val onUserModeClick = {
                                val isLoggedIn =
                                    TokenManager.getInstance(this@MainActivity).isLoggedIn()

                                if (isLoggedIn) {
                                    navigate(getString(R.string.user_mode_route))
                                } else {
                                    navigate(getString(R.string.login_route))
                                }
                            }

                            BackHandler(true) { }

                            LandingScreen(onChargerModeClick, onUserModeClick)
                        }
                        composable(getString(R.string.scan_card_route)) {
                            val onScan = { navigate(getString(R.string.charger_mode_route)) }

                            ScanScreen(
                                stringResource(R.string.charger_mode),
                                null,
                                { navigate(getString(R.string.landing_route)) },
                                onScan,
                                scanViewModel,
                                cardManagers,
                            )
                        }
                        composable(getString(R.string.register_card_route)) {
                            val onScan = {
                                navigate(getString(R.string.registration_route))
                            }

                            ScanScreen(
                                stringResource(R.string.scan_card),
                                LastRegisteredCard,
                                { navigate(getString(R.string.registration_route)) },
                                onScan,
                                scanViewModel,
                                cardManagers,
                            )
                        }
                        composable(getString(R.string.add_card_route)) {
                            val onScan = {
                                navigate(getString(R.string.my_cards_route))
                            }

                            ScanScreen(
                                stringResource(R.string.scan_card),
                                LastAddedCard,
                                { navigate(getString(R.string.my_cards_route)) },
                                onScan,
                                scanViewModel,
                                cardManagers,
                            )
                        }
                        composable(getString(R.string.charger_mode_route)) {
                            ChargerScreen({
                                navigate(getString(R.string.scan_card_route))
                            }, {
                                navigate(getString(R.string.ev_simulator_route))
                            }, chargerViewModel)
                        }
                        composable(getString(R.string.registration_route)) {
                            val onLogInClick = { navigate(getString(R.string.login_route)) }
                            var onAddCard = { navigate(getString(R.string.register_card_route)) }
                            val onArrowBackClick = { navigate(getString(R.string.landing_route)) }

                            RegistrationScreen(
                                onArrowBackClick,
                                onLogInClick,
                                onAddCard,
                                authenticationViewModel,
                            )
                        }
                        composable(getString(R.string.login_route)) {
                            val onRegisterClick =
                                { navigate(getString(R.string.registration_route)) }
                            val onLogin = { navigate(getString(R.string.user_mode_route)) }
                            LoginScreen(
                                onRegisterClick,
                                onLogin,
                                { navigate(getString(R.string.landing_route)) },
                                authenticationViewModel,
                            )
                        }
                        composable(getString(R.string.user_mode_route)) {
                            val onHistoryClick =
                                { navigate(getString(R.string.charging_history_route)) }
                            val onCardsClick = { navigate(getString(R.string.my_cards_route)) }
                            val onArrowBackClick = { navigate(getString(R.string.landing_route)) }

                            BackHandler(true) { }
                            UserModeScreen(
                                onHistoryClick,
                                onCardsClick,
                                onArrowBackClick,
                            )
                        }
                        composable(getString(R.string.charging_history_route)) {
                            HistoryScreen({
                                navigate(getString(R.string.user_mode_route))
                            }, onLogOut, historyViewModel)
                        }
                        composable(getString(R.string.ev_simulator_route)) {
                            SimulatorScreen(chargerViewModel) {
                                navigate(getString(R.string.charger_mode_route))
                            }
                        }
                        composable(getString(R.string.my_cards_route)) {
                            val onAddCard = { navigate(getString(R.string.add_card_route)) }
                            CardScreen({
                                navigate(getString(R.string.user_mode_route))
                            }, onAddCard, onLogOut, cardViewModel)
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
