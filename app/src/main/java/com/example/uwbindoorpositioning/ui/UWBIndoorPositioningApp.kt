package com.example.uwbindoorpositioning.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.screens.anchor.AnchorCoordinatesScreen
import com.example.uwbindoorpositioning.ui.screens.anchor.AnchorSearchScreen
import com.example.uwbindoorpositioning.ui.screens.responder.ResponderScreen
import com.example.uwbindoorpositioning.ui.screens.start.StartScreen
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.UWBIncapableScreen
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.UWBOffScreen

/**
 * Enum class that defines the screens for the Navhost
 */
enum class Screen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Responder(title = R.string.uwb_responder),
    AnchorCoordinates(title = R.string.uwb_anchor),
    AnchorSearch(title = R.string.uwb_anchor),
    UWBIncapable(title = R.string.app_name),
    UWBOff(title = R.string.app_name),
}

@Composable
fun AppBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                }
            }
        },
        actions = {
            TextButton(onClick = { /*TODO*/ }) {
                // TODO Change displayed icon, content description and text if dark mode is changed
                Icon(
                    imageVector = Icons.Filled.LightMode,
                    contentDescription = stringResource(R.string.light_mode),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.light_mode))
            }
        }
    )
}

@Composable
fun UWBIndoorPositioningApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Start.name
    )

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                // navController.previousBackStackEntry gets the past shown screen
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        // This is where the app's content will be displayed and switched
        NavHost(
            navController = navController,
            startDestination = Screen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Start.name) {
                StartScreen(
                    onUWBResponderButtonClicked = { navController.navigate(Screen.Responder.name) },
                    onUWBAnchorButtonClicked = { navController.navigate(Screen.AnchorCoordinates.name) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = Screen.Responder.name) {
                ResponderScreen(
                    // TODO Add button onClicks (do I need to declare this here for maps and going to settings?)
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = Screen.AnchorCoordinates.name) {
                AnchorCoordinatesScreen(
                    onStartButtonClicked = { navController.navigate(Screen.AnchorSearch.name) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = Screen.AnchorSearch.name) {
                AnchorSearchScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = Screen.UWBIncapable.name) {
                UWBIncapableScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = Screen.UWBOff.name) {
                UWBOffScreen(
                    // TODO Add button onClicks (do I need to declare this here for going to settings?)
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}