package com.phototracker.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.phototracker.app.ui.detail.DayDetailScreen
import com.phototracker.app.ui.home.HomeScreen

private const val ROUTE_HOME = "home"
private const val ROUTE_DAY = "day/{day}"

@Composable
fun PhotoTrackerApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(onDayClick = { day -> navController.navigate("day/$day") })
        }
        composable(
            route = ROUTE_DAY,
            arguments = listOf(navArgument("day") { type = NavType.IntType }),
        ) { backStackEntry ->
            val day = backStackEntry.arguments?.getInt("day") ?: 1
            DayDetailScreen(day = day, onBack = { navController.popBackStack() })
        }
    }
}
