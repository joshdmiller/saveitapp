package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

case class User (id: Long)
case class Item (id: String, user: User)

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

  def get_item_by_id(user_id: Long, id: String) = {
    //FIXME: static item matching
    id match {
      case "valid-item" => Some(Item(id, get_user_by_id(user_id).get))
      case _            => None
    }
  }

  def user_to_jsobject(user: User) = {
    JsObject(
      "id" -> JsString(get_user_uri(user.id)) ::
      Nil
    )
  }

  def item_to_jsobject(item: Item) = {
    JsObject(
      "id" -> JsString(get_item_uri(item.user.id, item.id)) ::
      "user" -> user_to_jsobject(item.user) ::
      Nil
    )
  }

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

  def get_user(id: Long) = Action {
    get_user_by_id(id) match {
      case Some(user) => Ok(
        success_message ++ user_to_jsobject(user)
      )
      case _ => NotFound(
        error_message(404, "User ["+get_user_uri(id)+"] not found.")
      )
    }
  }

  def get_items(user_id: Long) = Action {
    // FIXME: static list of items
    get_user_by_id(user_id) match {
      case Some(user) => Ok(
        JsObject(
          "user"    -> user_to_jsobject(user) ::
          "items"   -> JsArray(
            JsString(get_item_uri(user_id, "this-is-the-first-article-id")) ::
            JsString(get_item_uri(user_id, "another-article-id")) ::
            JsString(get_item_uri(user_id, "and-finally-a-third-article-id")) ::
            Nil
          ) ::
          Nil
        ) ++ success_message
      )
      case _ => NotFound(
        error_message(404, "User ["+get_user_uri(user_id)+"] not found.")
      )
    }
  }

  def get_item(user_id: Long, id: String) = Action {
    // FIXME: static item
    get_item_by_id(user_id, id) match {
      case Some(item) => Ok(
        success_message ++ item_to_jsobject(item)
      )
      case _ => NotFound(
        error_message(404, "Item ["+get_item_uri(user_id, id)+"] not found.")
      )
    }
  }
  
}
