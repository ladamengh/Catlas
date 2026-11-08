package com.ladamengh.petproject.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.di.appModule
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
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun CatlasApp(
    database: CatlasDatabase,
) {
    KoinApplication(configuration = koinConfiguration { modules(appModule(database)) }) {
        val navController = rememberNavController()

        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = CatlasColors.screenBackground,
            ) {
                NavHost(
                    navController = navController,
                    startDestination = AppTab.Home.route,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AppTab.entries.forEach { tab ->
                        composable(route = tab.route) {
                            MainLayout(
                                selectedTab = tab,
                                onTabSelected = { selectedTab ->
                                    navController.navigate(selectedTab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            ) {
                                when (tab) {
                                    AppTab.Home -> HomeFeedRoute(
                                        onNavigateToCat = { catId ->
                                            navController.navigate(AppRoutes.catDetails(catId))
                                        },
                                    )

                                    AppTab.Catalog -> CatalogRoute(
                                        onNavigateToBreed = { breedId ->
                                            navController.navigate(AppRoutes.breedDetails(breedId))
                                        },
                                    )

                                    AppTab.Favorites -> FavoritesRoute(
                                        onNavigateToCat = { catId ->
                                            navController.navigate(AppRoutes.catDetails(catId))
                                        },
                                        onNavigateToBreed = { breedId ->
                                            navController.navigate(AppRoutes.breedDetails(breedId))
                                        },
                                    )

                                    AppTab.Camera -> CameraRoute(
                                        onPublished = {
                                            navController.navigate(AppTab.Home.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                    )

                                    AppTab.Profile -> ProfileRoute()

                                }
                            }
                        }
                    }

                    composable(
                        route = AppRoutes.CatDetails,
                        arguments = listOf(navArgument("catId") { type = NavType.IntType }),
                    ) { backStackEntry ->
                        val catId = backStackEntry.arguments?.read { getIntOrNull("catId") }
                        if (catId != null) {
                            CatDetailsRoute(
                                catId = catId,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }

                    composable(
                        route = AppRoutes.BreedDetails,
                        arguments = listOf(navArgument("breedId") { type = NavType.StringType }),
                    ) { backStackEntry ->
                        val breedId = backStackEntry.arguments?.read { getStringOrNull("breedId") }
                        if (breedId != null) {
                            BreedDetailsRoute(
                                breedId = breedId,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                }
            }
        }
    }
}
