package com.ralphdugue.arcadephito.backend.adapters.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ContactDirective
import com.expediagroup.graphql.server.Schema

@ContactDirective(
    name = "Ralph Dugue",
    url = "https://ralphdugue.com/",
    description = "send urgent issues to rdugue1@gmail.com."
)
@GraphQLDescription("My schema description")
class ArcadePhitoSchema : Schema