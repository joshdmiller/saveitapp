import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.mvc.Results._

object Global extends GlobalSettings {
  override def onHandlerNotFound(request: RequestHeader): Result = {
    NotFound(
      JsObject(
        "status" -> JsNumber(404) ::
        "message" -> JsString("Resource ["+request.path+"] not found.") ::
        Nil
      )
    )
  }
}
