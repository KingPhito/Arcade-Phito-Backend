package com.ralphdugue.arcadephitogrpc.domain

import kotlinx.coroutines.flow.Flow

interface CoroutinesUseCase<P: Entity, R> {

    suspend fun execute(param: P): R
}

interface FlowUseCase<P: Entity, R: Entity> {

    fun execute(param: P): Flow<R>
}

object NoParams : Entity
object NoReturn : Entity