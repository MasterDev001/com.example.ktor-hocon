package route

import data.model.Note
import data.model.SimpleResponse
import data.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import repository.Repo

const val NOTES = "$API_VERSION/notes"
const val CREATE_NOTES = "$NOTES/create"
const val UPDATE_NOTES = "$NOTES/update"
const val DELETE_NOTES = "$NOTES/delete"

@Location(NOTES)
class NoteGetRoute

@Location(CREATE_NOTES)
class NoteCreateNote

@Location(UPDATE_NOTES)
class NoteUpdateNote

@Location(DELETE_NOTES)
class NoteDeleteNote


fun Route.NoteRoutes(db: Repo, hashFunction: (String) -> String) {

    authenticate("jwt") {

        location<NoteCreateNote> {
            method(HttpMethod.Post) {
                handle {
                    val note = try {
                        call.receive<Note>()
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, e.message.toString()))
                        return@handle
                    }

                    try {
                        val email = call.principal<User>()!!.email
                        db.addNote(note, email)
                        call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note Added Successfully!"))
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            SimpleResponse(false, e.message ?: "Some Problem Occurred")
                        )
                    }
                }
            }
        }


        get<NoteGetRoute> {
            try {
                val email = call.principal<User>()!!.email
                val notes = db.getAllNotes(email)
                call.respond(HttpStatusCode.OK, notes)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Some Problem Occurred")
            }

        }


        location<NoteUpdateNote> {
            method(HttpMethod.Post) {
                handle {
                    val note = try {
                        call.receive<Note>()
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, e.message.toString()))
                        return@handle
                    }

                    try {
                        val email = call.principal<User>()!!.email
                        db.updateNote(note, email)
                        call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note Updated Successfully!"))
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            SimpleResponse(false, e.message ?: "Some Problem Occurred")
                        )
                    }
                }
            }
        }


        delete<NoteDeleteNote> {
            val noteId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "QueryParameters: id is not present"))
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                db.deleteNote(noteId, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note Deleted Successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem ocurred"))
            }
        }
    }
}
