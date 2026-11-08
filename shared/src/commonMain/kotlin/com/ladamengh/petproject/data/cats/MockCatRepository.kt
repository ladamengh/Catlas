package com.ladamengh.petproject.data.cats

import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MockCatRepository : CatRepository {
    private val _cats = MutableStateFlow(
        listOf(
            Cat(
                id = 1,
                name = "Маруся",
                breed = "Британская короткошёрстная",
                location = "Москва",
                description = "Спокойная и любопытная кошка. Любит солнечные подоконники и мягкие пледы.",
            ),
            Cat(
                id = 2,
                name = "Пиксель",
                breed = "Европейская короткошёрстная",
                location = "Санкт-Петербург",
                description = "Энергичный исследователь, который всегда находит самое уютное место в доме.",
            ),
            Cat(
                id = 3,
                name = "Тиша",
                breed = "Сибирская кошка",
                location = "Казань",
                description = "Дружелюбный пушистик и большой ценитель внимания и вкусных угощений.",
            ),
            Cat(
                id = 4,
                name = "Луна",
                breed = "Бенгальская кошка",
                location = "Москва",
                description = "Подвижная и умная кошка, которая любит игры и новые впечатления.",
            ),
            Cat(
                id = 5,
                name = "Сёма",
                breed = "Мейн-кун",
                location = "Екатеринбург",
                description = "Большой добряк с мягким характером и очень выразительным взглядом.",
            ),
            Cat(
                id = 6,
                name = "Тося",
                breed = "Шотландская вислоухая",
                location = "Нижний Новгород",
                description = "Нежная и тихая кошка, которая обожает дремать рядом с человеком.",
            ),
            Cat(
                id = 7,
                name = "Граф",
                breed = "Русская голубая",
                location = "Самара",
                description = "Независимый красавец, но своим близким он дарит много тепла.",
            ),
            Cat(
                id = 8,
                name = "Карамелька",
                breed = "Сфинкс",
                location = "Новосибирск",
                description = "Общительная и ласковая кошка, которая любит уютные объятия.",
            ),
        ),
    )
    override val cats = _cats.asStateFlow()

    private val _breeds = MutableStateFlow(MockCatBreeds.all)
    override val breeds = _breeds.asStateFlow()

    private val _favoriteCatIds = MutableStateFlow<Set<Int>>(emptySet())
    override val favoriteCatIds = _favoriteCatIds.asStateFlow()

    private val _favoriteBreeds = MutableStateFlow<Set<String>>(emptySet())
    override val favoriteBreeds = _favoriteBreeds.asStateFlow()

    override suspend fun refreshBreeds() = Unit

    override fun toggleFavoriteCat(catId: Int) {
        _favoriteCatIds.update { ids ->
            if (catId in ids) ids - catId else ids + catId
        }
    }

    override fun toggleFavoriteBreed(breed: String) {
        _favoriteBreeds.update { breeds ->
            if (breed in breeds) breeds - breed else breeds + breed
        }
    }
}
