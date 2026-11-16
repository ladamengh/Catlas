package com.ladamengh.petproject.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.ladamengh.petproject.feature.catalog.BreedDetailsRoute
import com.ladamengh.petproject.feature.catalog.CatalogRoute
import com.ladamengh.petproject.feature.camera.CameraRoute
import com.ladamengh.petproject.feature.favorites.FavoritesRoute
import com.ladamengh.petproject.feature.home.CatDetailsRoute
import com.ladamengh.petproject.feature.home.HomeFeedRoute
import com.ladamengh.petproject.feature.navigation.AppRoutes
import com.ladamengh.petproject.feature.navigation.AppTab
import com.ladamengh.petproject.feature.navigation.MainLayout
import com.ladamengh.petproject.feature.profile.ProfileRoute
import com.ladamengh.petproject.ui.theme.CatlasColors
import org.koin.compose.KoinIsolatedContext
import org.koin.core.KoinApplication
import androidx.navigation.NavGraphBuilder

@Composable
fun CatlasApp(koinApplication: KoinApplication) {
    KoinIsolatedContext(koinApplication) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = CatlasColors.screenBackground,
            ) {
                CatlasNavigation()
            }
        }
    }
}

@Composable
private fun CatlasNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppTab.Home.route,
        modifier = Modifier.fillMaxSize(),
    ) {
        addTabDestinations(navController)
        addDetailDestinations(navController)
    }
}

private fun NavGraphBuilder.addTabDestinations(navController: NavHostController) {
    AppTab.entries.forEach { tab ->
        composable(route = tab.route) {
            MainLayout(
                selectedTab = tab,
                onTabSelected = navController::navigateToTab,
            ) {
                TabContent(tab, navController)
            }
        }
    }
}

@Composable
private fun TabContent(tab: AppTab, navController: NavHostController) {
    when (tab) {
        AppTab.Home -> HomeFeedRoute { catId -> navController.navigate(AppRoutes.catDetails(catId)) }
        AppTab.Catalog -> CatalogRoute { breedId ->
            navController.navigate(AppRoutes.breedDetails(breedId))
        }
        AppTab.Favorites -> FavoritesRoute(
            onNavigateToCat = { catId -> navController.navigate(AppRoutes.catDetails(catId)) },
            onNavigateToBreed = { breedId -> navController.navigate(AppRoutes.breedDetails(breedId)) },
        )
        AppTab.Camera -> CameraRoute(onPublished = { navController.navigateToTab(AppTab.Home) })
        AppTab.Profile -> ProfileRoute()
    }
}

private fun NavGraphBuilder.addDetailDestinations(navController: NavHostController) {
    composable(
        route = AppRoutes.CatDetails,
        arguments = listOf(navArgument("catId") { type = NavType.IntType }),
    ) { backStackEntry ->
        val catId = backStackEntry.arguments?.read { getIntOrNull("catId") }
        if (catId != null) {
            CatDetailsRoute(catId = catId, onBack = navController::popBackStack)
        }
    }
    composable(
        route = AppRoutes.BreedDetails,
        arguments = listOf(navArgument("breedId") { type = NavType.StringType }),
    ) { backStackEntry ->
        val breedId = backStackEntry.arguments?.read { getStringOrNull("breedId") }
        if (breedId != null) {
            BreedDetailsRoute(breedId = breedId, onBack = navController::popBackStack)
        }
    }
}

private fun NavHostController.navigateToTab(tab: AppTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
