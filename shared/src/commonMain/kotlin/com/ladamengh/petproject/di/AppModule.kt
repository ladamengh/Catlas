package com.ladamengh.petproject.di

import com.ladamengh.petproject.data.cats.RoomCatRepository
import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.user.RoomProfileRepository
import com.ladamengh.petproject.domain.repository.CatRepository
import com.ladamengh.petproject.domain.repository.ProfileRepository
import com.ladamengh.petproject.feature.catalog.BreedDetailsViewModel
import com.ladamengh.petproject.feature.catalog.CatalogViewModel
import com.ladamengh.petproject.feature.favorites.FavoritesViewModel
import com.ladamengh.petproject.feature.home.CatDetailsViewModel
import com.ladamengh.petproject.feature.home.HomeFeedViewModel
import com.ladamengh.petproject.feature.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun appModule(database: CatlasDatabase) = module {
    single { database }
    single<CatRepository> { RoomCatRepository(get()) }
    single<ProfileRepository> { RoomProfileRepository(get()) }

    viewModel { HomeFeedViewModel(get()) }
    viewModel { CatalogViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { parameters ->
        CatDetailsViewModel(parameters.get<Int>(), get())
    }
    viewModel { parameters ->
        BreedDetailsViewModel(parameters.get<String>(), get())
    }
}
