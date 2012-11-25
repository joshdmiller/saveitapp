package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Application extends Controller {
  
  def index = Action {
    Ok("Save It App Home").as("text/html")
  }

  def get_user(id: Long) = Action {
    // FIXME: static user matching for development
    id match {
      case 1234 => Ok(
        JsObject(
          "status"  -> JsNumber(200) ::
          "message" -> JsString("Success") ::
          "user"    -> JsObject(
            "id"      -> JsNumber(id) ::
            Nil
          ) ::
          Nil
        )
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
