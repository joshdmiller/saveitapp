package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok("Save It App Home").as("text/html")
  }
  
}
