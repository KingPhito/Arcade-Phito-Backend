package com.ralphdugue.arcadephito.backend.modules

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import com.ralphdugue.arcadephito.backend.adapters.graphql.ArcadePhitoSchema
import com.ralphdugue.arcadephito.backend.adapters.graphql.mutations.CreateUserMutation
import com.ralphdugue.arcadephito.backend.adapters.graphql.queries.LoginUserQuery
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureSchema() {
    val createUserMutation: CreateUserMutation by inject()
    val loginUserQuery: LoginUserQuery by inject()

    install(GraphQL) {
        schema {
            packages = listOf("com.ralphdugue.arcadephito.backend")
            queries = listOf(loginUserQuery)
            mutations = listOf(createUserMutation)
            schemaObject = ArcadePhitoSchema()
        }
    }
    routing {
        graphQLPostRoute()
        graphQLGetRoute()
        graphiQLRoute()
    }
}