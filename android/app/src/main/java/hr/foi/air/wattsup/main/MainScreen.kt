package hr.foi.air.wattsup.main

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hr.foi.air.charging.ChargingModuleImpl
import hr.foi.air.core.AppMode
import hr.foi.air.user_mode.UserModuleImpl
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.navigation.NavigationItem
import hr.foi.air.wattsup.navigation.Screens
import hr.foi.air.wattsup.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val modules: List<AppMode> = listOf(
        ChargingModuleImpl(),
        UserModuleImpl(),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                navigationIcon = {
                    IconButton(onClick = { Log.i("NAV", "menu") }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Green.copy(alpha = 0.3f),
                ),
            )
        },

        bottomBar = {
            NavigationBar {
                NavigationItem().navigationItems().forEachIndexed { _, navigationItem ->
                    val selected = currentDestination?.hierarchy?.any { navigationItem.route == currentDestination?.route } == true
                    val iconResource = if (selected) navigationItem.selectedIconId else navigationItem.unselectedIconId

                    NavigationBarItem(
                        selected = navigationItem.route == currentDestination?.route,
                        label = {
                            Text(stringResource(navigationItem.labelId))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(iconResource),
                                contentDescription = stringResource(id = navigationItem.labelId),
                            )
                        },
                        onClick = {
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(paddingValues = paddingValues),
        ) {
            /*composable(Screens.Home.route) {
                HomeScreen(
                    navController,
                )
            }
            composable(Screens.Charger.route) {
                ChargerScreen(
                    navController,
                )
            }
            composable(Screens.History.route) {
                HistoryScreen(
                    navController,
                )
            }*/
            composable(Screens.Home.route) {
                HomeScreen(
                    navController,
                )
            }
            modules.forEach { module ->
                composable(module.getRoute()) {
                    module.displayUI()
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
