package com.filemanager.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.filemanager.core.navigation.destination.MAIN_SCREEN_ROUTE
import com.filemanager.core.navigation.destination.main
import com.filemanager.core.navigation.destination.navigateToMain

@Composable
fun AppNavigator(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = MAIN_SCREEN_ROUTE
    ) {
        main(
            onNavigateToMain = {
                navController.navigateToMain(it.path, it.name)
            },
            onNavigateBack = navController::navigateUp,
        )
    }
}