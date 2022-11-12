package route

import authentication.JwtService
import data.model.LoginRequest
import data.model.RegisterRequest
import data.model.SimpleResponse
import data.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.locations.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import repository.Repo

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"


@Location(REGISTER_REQUEST)
class UserRegisterRoute

@Location(LOGIN_REQUEST)
class UserLoginRoute

fun Route.UsersRoutes(db: Repo, jwtService: JwtService, hashFunction: (String) -> String) {

    location<UserRegisterRoute> {
        method(HttpMethod.Post) {
            handle {
                val registerRequest = try {
                    call.receive<RegisterRequest>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, e.message.toString()))
                    //"Missing Some Fields"
                    return@handle
                }
                try {
                    val user = User(registerRequest.email, hashFunction(registerRequest.password), registerRequest.name)
                    db.addUser(user)
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occured"))
                }
            }
        }
    }


    location<UserLoginRoute> {
        method(HttpMethod.Post) {
            handle {
                val loginRequest = try {
                    call.receive<LoginRequest>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, e.message.toString()))
                    //"Missing Some Fields"
                    return@handle
                }
                try {
                    val user = db.findUserByEmail(loginRequest.email)
                    if (user == null) {
                        call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Wrong Email id"))
                    } else {
                        if (user.hashPassword == hashFunction(loginRequest.password)) {
                            call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
                        } else {
                            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Password incorrect!"))
                        }
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occured"))
                }
            }
        }
    }

//    post<UserRegisterRoute> {    // bu yaramadi ichi tepada manual yozildi
//        val registerRequest = try {
//            call.receive<RegisterRequest>()
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, e.message.toString()))
//            return@post
//        }
//    }
}