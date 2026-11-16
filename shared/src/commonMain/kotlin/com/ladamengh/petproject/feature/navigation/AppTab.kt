package com.ladamengh.petproject.feature.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector
import com.ladamengh.petproject.resources.Res
import com.ladamengh.petproject.resources.tab_camera
import com.ladamengh.petproject.resources.tab_catalog
import com.ladamengh.petproject.resources.tab_favorites
import com.ladamengh.petproject.resources.tab_home
import com.ladamengh.petproject.resources.tab_profile
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

internal fun AppTab.toImageVector(): ImageVector? = when (this) {
    AppTab.Home -> Icons.Filled.Home
    AppTab.Catalog -> Icons.Filled.GridView
    AppTab.Camera -> Icons.Filled.PhotoCamera
    AppTab.Favorites -> Icons.Filled.Favorite
    AppTab.Profile -> null
}

object AppRoutes {
    const val CatDetails = "cat/{catId}"
    const val BreedDetails = "breed/{breedId}"

    fun catDetails(catId: Int): String = "cat/$catId"
    fun breedDetails(breedId: String): String = "breed/$breedId"
}
