package com.example.plugins

import authentication.JwtService
import io.ktor.server.sessions.*
import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*
import io.ktor.server.locations.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import repository.Repo

fun Application.configureSecurity(jwtService: JwtService, db: Repo) {
    data class MySession(val count: Int = 0)

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Locations) //nimanidur install qilyabdi

    authentication {
        jwt("jwt") {
            val jwtAudience = this@configureSecurity.environment.config.property("jwt.audience").getString()
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
//            realm = this@configureSecurity.environment.config.property("Note Server").getString()

            verifier(jwtService.verifier)
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
            }


//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256("secret"))
//                    .withAudience(jwtAudience)
//                    .withIssuer(this@configureSecurity.environment.config.property("jwt.domain").getString())
//                    .build()
//            )
//            validate { credential ->
//                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
//            }
        }
    }

    routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
    }
}
