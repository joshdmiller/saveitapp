package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

case class User (id: Long)

object Application extends Controller {

  def get_user_by_id(id: Long) = {
    // FIXME: static user matching for development
    id match {
      case 1234 => Some(User(id))
      case _    => None
    }
  }

  def user_to_jsobject(user: User) = {
    JsObject(
      "id" -> JsNumber(user.id) ::
      Nil
    )
  }

  def success_message = JsObject(
    "status"  -> JsNumber(200) ::
    "message" -> JsString("Success") ::
    Nil
  )

  def index = Action {
    Ok("Save It App Home").as("text/html")
  }

  def get_user(id: Long) = Action {
    get_user_by_id(id) match {
      case Some(user) => Ok(
        JsObject(
          "user"    -> user_to_jsobject(user) ::
          Nil
        ) ++ success_message
      )
      case _ => NotFound(
        JsObject(
          "status" -> JsNumber(404) ::
          "message" -> JsString("User ["+id+"] not found.") ::
          Nil
        )
      )
    }
  }

  def get_items(id: Long) = Action {
    // FIXME: static list of items
    get_user_by_id(id) match {
      case Some(user) => Ok(
        JsObject(
          "user"    -> user_to_jsobject(user) ::
          "items"   -> JsArray(
            JsString("this-is-the-first-article-id") ::
            JsString("another-article-id") ::
            JsString("and-finally-a-third-article-id") ::
            Nil
          ) ::
          Nil
        ) ++ success_message
      )
      case _ => NotFound(
        JsObject(
          "status" -> JsNumber(404) ::
          "message" -> JsString("User ["+id+"] not found.") ::
          Nil
        )
      )
    }
  }
  
}
