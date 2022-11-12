package data.model

import io.ktor.server.auth.*

data class User(
    val email: String,
    val hashPassword: String,
    val userName: String
):Principal
