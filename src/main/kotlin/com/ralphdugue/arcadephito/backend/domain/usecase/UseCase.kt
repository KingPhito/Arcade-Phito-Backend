package com.ralphdugue.arcadephito.backend.domain.usecase

import com.ralphdugue.arcadephito.backend.domain.entities.Entity

interface UseCase<P : Entity, R : Entity> {

    suspend fun execute(param: P): R
}