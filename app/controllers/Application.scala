package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

case class User (id: Long)

object Application extends Controller {

  def get_item_uri(userid: Long, itemid: String) = "/users/"+userid+"/items/"+itemid
  def get_user_uri(userid: Long) = "/users/"+userid

  def get_user_by_id(id: Long) = {
    // FIXME: static user matching for development
    id match {
      case 1234 => Some(User(id))
      case _    => None
    }
  }

  def user_to_jsobject(user: User) = {
    JsObject(
      "id" -> JsString(get_user_uri(user.id)) ::
      Nil
    )
  }

  def success_message = JsObject(
    "status"  -> JsNumber(200) ::
    "message" -> JsString("Success") ::
    Nil
  )

  /* Controller Methods */

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
          "message" -> JsString("User ["+get_user_uri(id)+"] not found.") ::
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
            JsString(get_item_uri(id, "this-is-the-first-article-id")) ::
            JsString(get_item_uri(id, "another-article-id")) ::
            JsString(get_item_uri(id, "and-finally-a-third-article-id")) ::
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
