package com.example

import authentication.JwtService
import io.ktor.server.application.*
import com.example.plugins.*
import repository.DatabaseFactory
import repository.Repo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    DatabaseFactory.init()
    val jwtService = JwtService()
    val db = Repo()

    configureSerialization()
    configureSecurity(jwtService,db)
    configureRouting(jwtService,db)

}
