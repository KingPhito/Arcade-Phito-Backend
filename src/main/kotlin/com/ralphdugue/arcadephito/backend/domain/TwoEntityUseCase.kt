package com.ralphdugue.arcadephito.backend.domain

interface TwoEntityUseCase<P : Entity, R : Entity> {

    suspend fun execute(param: P): R
}

interface EntityParamUseCase<P : Entity, R> {

    suspend fun execute(param: P): R
}

interface EntityReturnUseCase<R : Entity> {

    suspend fun execute(): R
}