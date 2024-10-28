package io.github.projectuniverse.uwbindoorpositioning.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.projectuniverse.uwbindoorpositioning.R
import io.github.projectuniverse.uwbindoorpositioning.data.AppTheme
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.anchor.AnchorCoordinatesScreen
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.anchor.AnchorCoordinatesViewModel
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.anchor.AnchorSearchScreen
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.anchor.AnchorSearchViewModel
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.responder.ResponderScreen
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.responder.ResponderViewModel
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.start.StartScreen
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.troubleshooting.PermissionsNotGrantedScreen
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.troubleshooting.PermissionsNotGrantedViewModel
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.troubleshooting.UWBErrorScreen
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.troubleshooting.UWBUnavailableScreen
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.troubleshooting.UWBUnavailableViewModel
import io.github.projectuniverse.uwbindoorpositioning.ui.theme.padding
import io.github.projectuniverse.uwbindoorpositioning.ui.theme.spacing
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
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.topAppBarSmallSpacerSize))
                Text(
                    text = stringResource(selectedTheme.title),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun UWBIndoorPositioningApp(
    isDeviceUWBCapable: Boolean,
    arePermissionsGranted: Boolean,
    doesDeviceSupportUWBRanging: Boolean?,
    isUWBAvailable: Boolean?,
    selectedTheme: AppTheme,
    setAppTheme: (AppTheme) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val isDark = when (selectedTheme) {
        AppTheme.MODE_AUTO -> isSystemInDarkTheme()
        AppTheme.MODE_DAY -> false
        AppTheme.MODE_NIGHT -> true
    }
    val startScreenRoute = StartScreen::class.qualifiedName.orEmpty()
    val responderScreenRoute = ResponderScreen::class.qualifiedName.orEmpty()
    val anchorCoordinatesScreenRoute = AnchorCoordinatesScreen::class.qualifiedName.orEmpty()
    val anchorSearchScreenRoute = AnchorSearchScreen::class.qualifiedName.orEmpty()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: startScreenRoute
    val currentScreen =
        if (responderScreenRoute.isNotEmpty() && currentRoute.contains(responderScreenRoute)) {
            Screen.valueOf(Screen.Responder.name)
        } else if ((anchorCoordinatesScreenRoute.isNotEmpty() && currentRoute.contains(anchorCoordinatesScreenRoute)) ||
                    (anchorSearchScreenRoute.isNotEmpty() && currentRoute.contains(anchorSearchScreenRoute))) {
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
        },
        modifier = modifier
    ) { innerPadding -> // Prevents the top app bar from covering content
        /*
         * Ensures that all screens using rootModifier fill their max size and have padding,
         * regardless of their own size calls. Accepting a modifier and having the the parent
         * composable tell the child composable how to measure follows best practices.
         */
        val rootModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(
                top = MaterialTheme.padding.verticalPadding,
                bottom = MaterialTheme.padding.verticalPadding,
                start = MaterialTheme.padding.horizontalPadding,
                end = MaterialTheme.padding.horizontalPadding
            )

        // Show screens in a very specific order that ensures the app functions properly
        if (!isDeviceUWBCapable) {
            UWBErrorScreen(
                errorMessage = stringResource(R.string.no_uwb_support),
                modifier = rootModifier
            )
        }
        else if (!arePermissionsGranted) {
            if (hasNavGraphBeenBuilt(navController)) {
                navController.popBackStack(StartScreen, false)
            }
            val viewModel = hiltViewModel<PermissionsNotGrantedViewModel>()
            PermissionsNotGrantedScreen(
                viewModel = viewModel,
                modifier = rootModifier
            )
        }
        else if (isUWBAvailable == null || !isUWBAvailable) {
            if (isUWBAvailable != null) {
                if (hasNavGraphBeenBuilt(navController)) {
                    navController.popBackStack(StartScreen, false)
                }
                val viewModel = hiltViewModel<UWBUnavailableViewModel>()
                UWBUnavailableScreen(
                    viewModel = viewModel,
                    modifier = rootModifier
                )
            }
        }
        else if (doesDeviceSupportUWBRanging == null || !doesDeviceSupportUWBRanging) {
            if (doesDeviceSupportUWBRanging != null) {
                UWBErrorScreen(
                    errorMessage = stringResource(R.string.device_does_not_support_uwb_ranging),
                    modifier = rootModifier
                )
            }
        }
        else {
            NavHost(
                navController = navController,
                startDestination = StartScreen
            ) {
                composable<StartScreen> {
                    StartScreen(
                        onUWBResponderButtonClicked = { navController.navigate(ResponderScreen) },
                        onUWBAnchorButtonClicked = { navController.navigate(AnchorCoordinatesScreen) },
                        modifier = rootModifier
                    )
                }
                composable<ResponderScreen> {
                    val viewModel = hiltViewModel<ResponderViewModel>()
                    ResponderScreen(
                        isDark = isDark,
                        viewModel = viewModel,
                        modifier = rootModifier
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
                        modifier = rootModifier
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
                        modifier = rootModifier
                    )
                }
            }
        }
    }
}

private fun hasNavGraphBeenBuilt(navController: NavHostController): Boolean {
    try {
        navController.graph
        return true
    }  catch (exception: IllegalStateException) {
        return false
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