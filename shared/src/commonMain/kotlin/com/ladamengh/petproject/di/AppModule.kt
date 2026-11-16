package com.ladamengh.petproject.di

import com.ladamengh.petproject.data.cats.RoomCatRepository
import com.ladamengh.petproject.data.cats.SupabaseCatDataSource
import com.ladamengh.petproject.data.cats.remote.SupabaseBreedsService
import com.ladamengh.petproject.data.cats.remote.SupabaseCatsService
import com.ladamengh.petproject.data.cats.remote.SupabaseConfig
import com.ladamengh.petproject.data.cats.remote.createSupabaseHttpClient
import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.user.RoomProfileRepository
import com.ladamengh.petproject.domain.repository.CatRepository
import com.ladamengh.petproject.domain.repository.ProfileRepository
import com.ladamengh.petproject.feature.catalog.BreedDetailsViewModel
import com.ladamengh.petproject.feature.catalog.CatalogViewModel
import com.ladamengh.petproject.feature.camera.CameraViewModel
import com.ladamengh.petproject.feature.favorites.FavoritesViewModel
import com.ladamengh.petproject.feature.home.CatDetailsViewModel
import com.ladamengh.petproject.feature.home.HomeFeedViewModel
import com.ladamengh.petproject.feature.profile.ProfileViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.onClose

fun appModule(
    database: CatlasDatabase,
    projectUrl: String = SupabaseConfig.projectUrl,
    publishableKey: String = SupabaseConfig.publishableKey,
    httpClientFactory: () -> HttpClient = ::createSupabaseHttpClient,
) = module {
    single { database } onClose { it?.close() }
    single { httpClientFactory() } onClose { it?.close() }
    single { SupabaseBreedsService(get(), projectUrl, publishableKey) }
    single { SupabaseCatsService(get(), projectUrl, publishableKey) }
    single { SupabaseCatDataSource(get(), get()) }
    single { RoomCatRepository(get(), get()) } onClose { it?.close() }
    single<CatRepository> { get<RoomCatRepository>() }
    single<ProfileRepository> { RoomProfileRepository(get()) }

    viewModel { HomeFeedViewModel(get()) }
    viewModel { CatalogViewModel(get()) }
    viewModel { CameraViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { parameters ->
        CatDetailsViewModel(parameters.get<Int>(), get())
    }
    viewModel { parameters ->
        BreedDetailsViewModel(parameters.get<String>(), get())
    }
}
