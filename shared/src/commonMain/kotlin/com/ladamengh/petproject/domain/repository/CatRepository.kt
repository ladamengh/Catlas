package com.ladamengh.petproject.domain.repository

import com.ladamengh.petproject.domain.model.Cat

interface CatRepository {
    fun getCats(): List<Cat>
}
