package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

import models._
import com.mongodb.casbah.Imports._

object Application extends Controller {

  def success_message = JsObject(
    "status"  -> JsNumber(200) ::
    "message" -> JsString("Success") ::
    Nil
  )
  
  def error_message(status: Int, msg: String) = JsObject(
    "status"  -> JsNumber(status) ::
    "message" -> JsString(msg) ::
    Nil
  )

  /* Controller Methods */

  def index = Action {
    Ok("Save It App Home").as("text/html")
  }

  def get_user(username: String) = Action {
    UserDAO.by_username(username) match {
      case Some(user) => Ok(
        success_message ++ UserDAO.to_jsobject(user)
      )
      case _ => NotFound(
        error_message(404, "User [/users/"+username+"] not found.")
      )
    }
  }

  def create_user = Action { implicit request =>
    request.body.asJson.map { json =>
      (json \ "username").asOpt[String].map { un =>
        UserDAO.insert(User(id = new ObjectId, username = un)) match {
          case Some(oid) => Ok(
            success_message
          )
          case _ => {
            // FIXME: better error reporting
            InternalServerError("Could not create user.")
          }
        }
      }.getOrElse {
        BadRequest("Missing parameter [username]")
      }
    }.getOrElse {
      BadRequest("Requests must be made in JSON.")
    }
  }

  def get_items(username: String) = Action {
    // FIXME: static list of items
    UserDAO.by_username(username) match {
      case Some(user) => {
        val items = new collection.mutable.ListBuffer[JsString]()
        val query = ItemDAO.find(MongoDBObject("user_id" -> user.id))

        // iterate query with foreach
        query.foreach { i =>
          // for each, add its string ID to a Seq[JsString]
          items += JsString("/users/"+username+"/items/"+i.id.toString)
        }

        Ok(success_message ++ JsObject(
          "user"    -> UserDAO.to_jsobject(user) ::
          "items"   -> JsArray(
            items
          ) ::
          Nil
        ))
      }
      
      case _ => NotFound(
        error_message(404, "User [/users/"+username+"] not found.")
      )
    }
  }

  def get_item(username: String, id: String) = Action {
    ItemDAO.by_id(id) match {
      case Some(item) => Ok(
        success_message ++ ItemDAO.to_jsobject(item)
      )
      case _ => NotFound(
        error_message(404, "Item [/users/"+username+"/items/"+id+"] not found.")
      )
    }
  }
  
}
