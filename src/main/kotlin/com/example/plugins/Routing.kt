package com.example.plugins

import authentication.JwtService
import authentication.hash
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import repository.Repo
import route.NoteRoutes
import route.UsersRoutes

fun Application.configureRouting(jwtService: JwtService, db: Repo) {

    val hashFunction = { s: String -> hash(s) }


    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        UsersRoutes(db, jwtService, hashFunction)
        NoteRoutes(db, hashFunction)

        route("/notes") {

            route("/create") {
                // localhost:8081/notes/create
                post {
                    val body = call.receive<String>()
                    call.respond(body)
                }
            }

            delete {
                val body = call.receive<String>()
                call.respond(body)
            }
        }
    }
}
