package com.ladamengh.petproject.feature.navigation

import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.tab_camera
import catlas.shared.generated.resources.tab_catalog
import catlas.shared.generated.resources.tab_favorites
import catlas.shared.generated.resources.tab_home
import catlas.shared.generated.resources.tab_profile
import org.jetbrains.compose.resources.StringResource

enum class AppTab(
    val route: String,
    val title: StringResource,
) {
    Home(route = "home", title = Res.string.tab_home),
    Catalog(route = "catalog", title = Res.string.tab_catalog),
    Camera(route = "camera", title = Res.string.tab_camera),
    Favorites(route = "favorites", title = Res.string.tab_favorites),
    Profile(route = "profile", title = Res.string.tab_profile),
}

object AppRoutes {
    const val CatDetails = "cat/{catId}"

    fun catDetails(catId: Int): String = "cat/$catId"
}
