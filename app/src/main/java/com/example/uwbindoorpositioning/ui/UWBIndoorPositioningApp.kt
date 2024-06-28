package com.example.uwbindoorpositioning.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.data.AppTheme
import com.example.uwbindoorpositioning.ui.screens.anchor.AnchorCoordinatesScreen
import com.example.uwbindoorpositioning.ui.screens.anchor.AnchorSearchScreen
import com.example.uwbindoorpositioning.ui.screens.anchor.AnchorViewModel
import com.example.uwbindoorpositioning.ui.screens.responder.ResponderScreen
import com.example.uwbindoorpositioning.ui.screens.start.StartScreen
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.UWBIncapableScreen
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.UWBOffScreen
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.UWBOffViewModel

// Enum class that defines the screens for the Navhost
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                        AppTheme.MODE_AUTO -> Icons.Filled.Settings
                        AppTheme.MODE_DAY -> Icons.Filled.LightMode
                        AppTheme.MODE_NIGHT -> Icons.Filled.DarkMode
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
    selectedTheme: AppTheme,
    setAppTheme: (AppTheme) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Start.name
    )

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
        // TODO Should I use innerpadding so that I don't have to set a padding for each screen manually?
        // This is where the app's content will be displayed and switched
        NavHost(
            navController = navController,
            startDestination = Screen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Start.name) {
                StartScreen(
                    onUWBResponderButtonClicked = { navController.navigate(route = Screen.Responder.name) },
                    onUWBAnchorButtonClicked = { navController.navigate(route = "Anchor") },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = Screen.Responder.name) {
                ResponderScreen(
                    // TODO Add button onClicks (do I need to declare this here for maps and going to settings?)
                    modifier = Modifier.fillMaxSize()
                )
            }
            /*
             * Use navigation to retrieve same instance of AnchorViewModel across multiple anchor
             * screens, scoped to navigation routes.
             */
            navigation(
                route = "Anchor", // Route to this nested graph within the main graph
                startDestination = Screen.AnchorCoordinates.name // First screen in this graph
            ) {
                composable(route = Screen.AnchorCoordinates.name) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("Anchor")
                    }
                    val parentViewModel = hiltViewModel<AnchorViewModel>(parentEntry)
                    AnchorCoordinatesScreen(
                        onStartButtonClicked = { navController.navigate(route = Screen.AnchorSearch.name) },
                        modifier = Modifier.fillMaxSize(),
                        viewModel = parentViewModel
                    )
                }
                composable(route = Screen.AnchorSearch.name) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("Anchor")
                    }
                    val parentViewModel = hiltViewModel<AnchorViewModel>(parentEntry)
                    AnchorSearchScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = parentViewModel
                    )
                }
            }
            composable(route = Screen.UWBIncapable.name) {
                UWBIncapableScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = Screen.UWBOff.name) {
                val viewModel = hiltViewModel<UWBOffViewModel>()
                UWBOffScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel
                )
            }
        }
    }
}