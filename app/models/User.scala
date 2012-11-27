package models

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection

import play.api.Play
import play.api.libs.json._

case class User (@Key("_id") id: ObjectId = new ObjectId, username: String)

object UserDAO extends SalatDAO[User, ObjectId](collection = MongoConnection()(
  Play.current.configuration.getString("mongo.db").get
)("users")) {

  def by_username(username: String) = {
    UserDAO.findOne(MongoDBObject("username" -> username))
  }

  def by_id(oid: ObjectId) = {
    UserDAO.findOne(MongoDBObject("_id" -> oid))
  }

  def to_jsobject(user: User) = {
    JsObject(
      "id" -> JsString("/users/"+user.username) ::
      Nil
    )
  }
}

