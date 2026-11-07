package com.ladamengh.petproject.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ladamengh.petproject.data.cats.MockCatRepository
import com.ladamengh.petproject.feature.home.CatDetailsScreen
import com.ladamengh.petproject.feature.home.HomeFeedScreen
import com.ladamengh.petproject.feature.navigation.AppRoutes
import com.ladamengh.petproject.feature.navigation.AppTab
import com.ladamengh.petproject.feature.navigation.MainLayout
import com.ladamengh.petproject.feature.placeholder.PlaceholderScreen
import com.ladamengh.petproject.ui.theme.CatlasColors
import kotlinx.coroutines.flow.collect

@Composable
fun CatlasApp(
    viewModel: CatlasViewModel = viewModel { CatlasViewModel(MockCatRepository()) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    LaunchedEffect(viewModel, navController) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is CatlasEffect.NavigateToTab -> navController.navigate(effect.tab.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }

                is CatlasEffect.NavigateToCat -> navController.navigate(
                    AppRoutes.catDetails(effect.catId),
                )
            }
        }
    }

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
                            onTabSelected = { viewModel.onIntent(CatlasIntent.SelectTab(it)) },
                        ) {
                            when (tab) {
                                AppTab.Home -> HomeFeedScreen(
                                    cats = uiState.cats,
                                    favoriteCatIds = uiState.favoriteCatIds,
                                    isLoading = uiState.isLoading,
                                    onIntent = viewModel::onIntent,
                                    modifier = Modifier.fillMaxSize(),
                                )

                                else -> PlaceholderScreen(
                                    title = tab.title,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }

                composable(
                    route = AppRoutes.CatDetails,
                    arguments = listOf(navArgument("catId") { type = NavType.IntType }),
                ) { backStackEntry ->
                    val catId = backStackEntry.arguments?.getInt("catId")
                    val cat = uiState.cats.firstOrNull { it.id == catId }

                    if (cat != null) {
                        CatDetailsScreen(
                            cat = cat,
                            isFavorite = cat.id in uiState.favoriteCatIds,
                            onBack = { navController.popBackStack() },
                            onFavoriteClick = {
                                viewModel.onIntent(CatlasIntent.ToggleFavorite(cat.id))
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding(),
                        )
                    }
                }
            }
        }
    }
}
