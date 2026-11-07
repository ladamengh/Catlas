package com.ladamengh.petproject.data.cats

import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.domain.repository.CatRepository

class MockCatRepository : CatRepository {
    override fun getCats(): List<Cat> = listOf(
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
    )
}
