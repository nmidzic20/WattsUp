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
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.foi.air.wattsup.ble.BLEManager
import hr.foi.air.wattsup.core.CardManager
import hr.foi.air.wattsup.di.dataModule
import hr.foi.air.wattsup.di.viewModelsModule
import hr.foi.air.wattsup.navigation.CustomNavDrawerItem
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.rfid.RFIDManager
import hr.foi.air.wattsup.screens.ChargerScreen
import hr.foi.air.wattsup.screens.LandingScreen
import hr.foi.air.wattsup.screens.LoginScreen
import hr.foi.air.wattsup.screens.RegistrationScreen
import hr.foi.air.wattsup.screens.ScanScreen
import hr.foi.air.wattsup.screens.SimulatorScreen
import hr.foi.air.wattsup.screens.UserModeScreen
import hr.foi.air.wattsup.ui.component.LogoutDialog
import hr.foi.air.wattsup.ui.theme.WattsUpTheme
import hr.foi.air.wattsup.ui.theme.colorDarkGray
import hr.foi.air.wattsup.utils.LastAddedCard
import hr.foi.air.wattsup.utils.LastRegisteredCard
import hr.foi.air.wattsup.viewmodels.CardViewModel
import hr.foi.air.wattsup.viewmodels.ChargerViewModel
import hr.foi.air.wattsup.viewmodels.HistoryViewModel
import hr.foi.air.wattsup.viewmodels.ScanViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {

    private val REQUEST_PERMISSIONS = 1
    private lateinit var REQUIRED_PERMISSIONS: List<String>

    private val chargerViewModel: ChargerViewModel by viewModels()
    private val scanViewModel: ScanViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val cardViewModel: CardViewModel by viewModels()
    // private val authenticationViewModel: AuthenticationViewModel by viewModels()

    private var cardManagers: List<CardManager> = emptyList()
    private var receivers: MutableList<BroadcastReceiver> =
        emptyList<BroadcastReceiver>().toMutableList()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(dataModule, viewModelsModule)
        }

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
                    val navigationState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    var selectedItemIndex by rememberSaveable {
                        mutableIntStateOf(R.string.landing_route)
                    }

                    val navController = rememberNavController()
                    val navigate = { routeId: Int ->
                        navController.navigate(getString(routeId))
                        selectedItemIndex = routeId
                    }

                    val showLogoutDialog = remember { mutableStateOf(false) }
                    var showLogoutNavDrawerItem = remember { mutableStateOf(false) }
                    val onLogOut = {
                        navigate(R.string.landing_route)
                        showLogoutNavDrawerItem.value = false
                    }
                    val resetUserScreenData = {
                        // To ensure API calls for events and cards are performed upon returning to UserModeView
                        historyViewModel.resetEvents()
                        cardViewModel.resetCards()
                    }
                    LogoutDialog(
                        showLogoutDialog,
                    ) {
                        onLogOut()
                        resetUserScreenData()
                    }

                    val onUserModeClick = {
                        val isLoggedIn =
                            TokenManager.getInstance(this@MainActivity).isLoggedIn()

                        if (isLoggedIn) {
                            navigate(R.string.user_mode_route)
                        } else {
                            navigate(R.string.login_route)
                        }
                    }

                    val items = listOf(
                        CustomNavDrawerItem(
                            title = "Home",
                            selectedIcon = Icons.Filled.Home,
                            unselectedIcon = Icons.Outlined.Home,
                            onClick = {
                                navigate(R.string.landing_route)
                                resetUserScreenData()
                            },
                            id = R.string.landing_route,
                        ),
                        CustomNavDrawerItem(
                            title = "User Mode",
                            selectedIcon = Icons.Filled.Person,
                            unselectedIcon = Icons.Outlined.Person,
                            onClick = onUserModeClick,
                            id = R.string.user_mode_route,
                        ),
                        CustomNavDrawerItem(
                            title = "Charger Mode",
                            selectedIcon = Icons.Filled.ElectricBolt,
                            unselectedIcon = Icons.Outlined.ElectricBolt,
                            onClick = {
                                navigate(R.string.scan_card_route)
                                resetUserScreenData()
                            },
                            id = R.string.scan_card_route,
                        ),
                        CustomNavDrawerItem(
                            title = getString(R.string.log_out),
                            selectedIcon = Icons.Filled.Logout,
                            unselectedIcon = Icons.Outlined.Logout,
                            onClick = {
                                showLogoutDialog.value = true
                            },
                            id = R.string.log_out,
                        ),
                    )

                    // upon starting ensure that user is logged out to prevent unauthorized responses when sending
                    // API requests with old token
                    if (TokenManager.getInstance(this@MainActivity).isLoggedIn()) {
                        TokenManager.getInstance(this@MainActivity).setJWTToken("")
                    }

                    val navDrawerImageId =
                        if (isSystemInDarkTheme()) R.drawable.logo_text_white else R.drawable.logo_text_green

                    ModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = MaterialTheme.colorScheme.background,
                                modifier = Modifier
                                    .width(300.dp)
                                    .fillMaxHeight(),
                            ) {
                                Spacer(modifier = Modifier.height(26.dp))
                                Image(
                                    painter = painterResource(id = navDrawerImageId),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .fillMaxWidth()
                                        .align(CenterHorizontally),
                                )
                                Spacer(modifier = Modifier.height(26.dp))
                                items.forEach { drawerItem ->
                                    if (drawerItem.title != getString(R.string.log_out) || showLogoutNavDrawerItem.value) {
                                        NavigationDrawerItem(
                                            colors = NavigationDrawerItemDefaults.colors(
                                                selectedIconColor = if (isSystemInDarkTheme()) colorDarkGray else Color.White,
                                                selectedTextColor = if (isSystemInDarkTheme()) colorDarkGray else Color.White,
                                                selectedContainerColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.secondary,
                                                unselectedContainerColor = MaterialTheme.colorScheme.background,
                                            ),
                                            label = {
                                                Text(text = drawerItem.title)
                                            },
                                            selected = drawerItem.id == selectedItemIndex,
                                            onClick = {
                                                selectedItemIndex = drawerItem.id
                                                scope.launch {
                                                    navigationState.close()
                                                    drawerItem.onClick()
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = if (drawerItem.id == selectedItemIndex) {
                                                        drawerItem.selectedIcon
                                                    } else {
                                                        drawerItem.unselectedIcon
                                                    },
                                                    contentDescription = drawerItem.title,
                                                )
                                            },
                                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                        )
                                    }
                                }
                            }
                        },
                        drawerState = navigationState,
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = getString(R.string.landing_route),
                        ) {
                            composable(getString(R.string.landing_route)) {
                                val onChargerModeClick =
                                    { navigate(R.string.scan_card_route) }
                                val onUserModeClick = {
                                    val isLoggedIn =
                                        TokenManager.getInstance(this@MainActivity).isLoggedIn()

                                    if (isLoggedIn) {
                                        navigate(R.string.user_mode_route)
                                    } else {
                                        navigate(R.string.login_route)
                                    }
                                }

                                BackHandler(true) { }

                                LandingScreen(onChargerModeClick, onUserModeClick)
                            }
                            composable(getString(R.string.scan_card_route)) {
                                val onScan =
                                    { navigate(R.string.charger_mode_route) }

                                ScanScreen(
                                    stringResource(R.string.charger_mode),
                                    null,
                                    { navigate(R.string.landing_route) },
                                    onScan,
                                    scanViewModel,
                                    cardManagers,
                                )
                            }
                            composable(getString(R.string.register_card_route)) {
                                val onScan = {
                                    navigate(R.string.registration_route)
                                }

                                ScanScreen(
                                    stringResource(R.string.scan_card),
                                    LastRegisteredCard,
                                    { navigate(R.string.registration_route) },
                                    onScan,
                                    scanViewModel,
                                    cardManagers,
                                )
                            }
                            composable(getString(R.string.add_card_route)) {
                                val onScan = {
                                    navigate(R.string.user_mode_route)
                                }

                                ScanScreen(
                                    stringResource(R.string.scan_card),
                                    LastAddedCard,
                                    { navigate(R.string.user_mode_route) },
                                    onScan,
                                    scanViewModel,
                                    cardManagers,
                                )
                            }
                            composable(getString(R.string.charger_mode_route)) {
                                ChargerScreen({
                                    navigate(R.string.scan_card_route)
                                }, {
                                    navigate(R.string.ev_simulator_route)
                                }, chargerViewModel)
                            }
                            composable(getString(R.string.registration_route)) {
                                val onLogInClick = { navigate(R.string.login_route) }
                                var onAddCard =
                                    { navigate(R.string.register_card_route) }
                                val onArrowBackClick =
                                    { navigate(R.string.landing_route) }

                                RegistrationScreen(
                                    onArrowBackClick,
                                    onLogInClick,
                                    onAddCard,
                                    viewModel = koinViewModel(),
                                    // authenticationViewModel,
                                )
                            }
                            composable(getString(R.string.login_route)) {
                                val onRegisterClick =
                                    { navigate(R.string.registration_route) }
                                val onLogin = {
                                    navigate(R.string.user_mode_route)
                                    showLogoutNavDrawerItem.value = true
                                }
                                LoginScreen(
                                    onRegisterClick,
                                    onLogin,
                                    { navigate(R.string.landing_route) },
                                    viewModel = koinViewModel(),
                                    // authenticationViewModel,
                                )
                            }
                            composable(getString(R.string.user_mode_route)) {
                                val onArrowBackClick =
                                    { navigate(R.string.landing_route) }
                                val onAddCard = { navigate(R.string.add_card_route) }

                                BackHandler(true) { }
                                UserModeScreen(
                                    historyViewModel,
                                    cardViewModel,
                                    onArrowBackClick,
                                    onLogOut,
                                    onAddCard,
                                    resetUserScreenData,
                                )
                            }
                            composable(getString(R.string.ev_simulator_route)) {
                                SimulatorScreen(chargerViewModel) {
                                    navigate(R.string.charger_mode_route)
                                }
                            }
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
