package com.example.uwbindoorpositioning.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.data.AppTheme
import com.example.uwbindoorpositioning.ui.screens.anchor.AnchorCoordinatesScreen
import com.example.uwbindoorpositioning.ui.screens.anchor.AnchorCoordinatesViewModel
import com.example.uwbindoorpositioning.ui.screens.anchor.AnchorSearchScreen
import com.example.uwbindoorpositioning.ui.screens.anchor.AnchorSearchViewModel
import com.example.uwbindoorpositioning.ui.screens.responder.ResponderScreen
import com.example.uwbindoorpositioning.ui.screens.responder.ResponderViewModel
import com.example.uwbindoorpositioning.ui.screens.start.StartScreen
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.PermissionsNotGrantedScreen
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.PermissionsNotGrantedViewModel
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.UWBIncapableScreen
import kotlinx.serialization.Serializable

// Enum class that defines the screens for the Navhost
enum class Screen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Responder(title = R.string.uwb_responder),
    Anchor(title = R.string.uwb_anchor),
}

@Composable
fun AppBar(
    selectedTheme: AppTheme,
    setAppTheme: (AppTheme) -> Unit,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                }
            }
        },
        actions = {
            TextButton(
                onClick = {
                    when (selectedTheme) {
                        AppTheme.MODE_AUTO -> setAppTheme(AppTheme.MODE_DAY)
                        AppTheme.MODE_DAY -> setAppTheme(AppTheme.MODE_NIGHT)
                        AppTheme.MODE_NIGHT -> setAppTheme(AppTheme.MODE_AUTO)
                    }
                }
            ) {
                Icon(
                    imageVector = when (selectedTheme) {
                        AppTheme.MODE_AUTO -> Icons.Rounded.Settings
                        AppTheme.MODE_DAY -> Icons.Rounded.LightMode
                        AppTheme.MODE_NIGHT -> Icons.Rounded.DarkMode
                    },
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(selectedTheme.title),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

@Composable
fun UWBIndoorPositioningApp(
    isDeviceUWBCapable: Boolean,
    arePermissionsGranted: Boolean,
    selectedTheme: AppTheme,
    setAppTheme: (AppTheme) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val isDark = when (selectedTheme) {
        AppTheme.MODE_AUTO -> isSystemInDarkTheme()
        AppTheme.MODE_DAY -> false
        AppTheme.MODE_NIGHT -> true
    }
    val startScreenRoute = StartScreen::class.qualifiedName.orEmpty()
    val responderScreenRoute = ResponderScreen::class.qualifiedName.orEmpty()
    // TODO name might need to be updated if screen does end up taking arguments
    val anchorCoordinatesScreenRoute = AnchorCoordinatesScreen::class.qualifiedName.orEmpty()
    val anchorSearchScreenRouteWithNoArguments = AnchorSearchScreen::class.qualifiedName.orEmpty()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: startScreenRoute
    val currentScreen =
        if (responderScreenRoute.isNotEmpty() && currentRoute.contains(responderScreenRoute)) {
            Screen.valueOf(Screen.Responder.name)
        } else if ((anchorCoordinatesScreenRoute.isNotEmpty() && currentRoute.contains(anchorCoordinatesScreenRoute)) ||
                    (anchorSearchScreenRouteWithNoArguments.isNotEmpty() && currentRoute.contains(anchorSearchScreenRouteWithNoArguments))) {
            Screen.valueOf(Screen.Anchor.name)
        } else {
            Screen.valueOf(Screen.Start.name)
        }

    Scaffold(
        topBar = {
            AppBar(
                selectedTheme = selectedTheme,
                setAppTheme = setAppTheme,
                currentScreen = currentScreen,
                // navController.previousBackStackEntry gets the past shown screen
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        // This is where the app's content will be displayed and switched
        // TODO Should I use innerpadding so that I don't have to set a padding for each screen manually?
        if (!isDeviceUWBCapable) {
            UWBIncapableScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
        else if (!arePermissionsGranted) {
            val viewModel = hiltViewModel<PermissionsNotGrantedViewModel>()
            PermissionsNotGrantedScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
        else {
            NavHost(
                navController = navController,
                startDestination = StartScreen,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<StartScreen> {
                    StartScreen(
                        onUWBResponderButtonClicked = { navController.navigate(ResponderScreen) },
                        onUWBAnchorButtonClicked = { navController.navigate(AnchorCoordinatesScreen) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                composable<ResponderScreen> {
                    val viewModel = hiltViewModel<ResponderViewModel>()
                    ResponderScreen(
                        isDark = isDark,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                composable<AnchorCoordinatesScreen> {
                    val viewModel = hiltViewModel<AnchorCoordinatesViewModel>()
                    AnchorCoordinatesScreen(
                        onStartButtonClicked = { anchorLatitude, anchorLongitude, anchorCompassBearing ->
                            navController.navigate(
                                AnchorSearchScreen(
                                    anchorLatitude = anchorLatitude,
                                    anchorLongitude = anchorLongitude,
                                    anchorCompassBearing = anchorCompassBearing
                                )
                            )
                        },
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Use navigation arguments to pass anchor latitude, longitude and compass bearing between screens
                composable<AnchorSearchScreen> { backStackEntry ->
                    val args = backStackEntry.toRoute<AnchorSearchScreen>()
                    val anchorLatitude = args.anchorLatitude
                    val anchorLongitude = args.anchorLongitude
                    val anchorCompassBearing = args.anchorCompassBearing
                    val viewModel = hiltViewModel<AnchorSearchViewModel, AnchorSearchViewModel.Factory>(
                        creationCallback = { factory ->
                            factory.create(
                                anchorLatitude = anchorLatitude,
                                anchorLongitude = anchorLongitude,
                                anchorCompassBearing = anchorCompassBearing
                            )
                        }
                    )
                    AnchorSearchScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Serializable
object StartScreen

@Serializable
object ResponderScreen

@Serializable
object AnchorCoordinatesScreen

@Serializable
data class AnchorSearchScreen(
    val anchorLatitude: String,
    val anchorLongitude: String,
    val anchorCompassBearing: String
)