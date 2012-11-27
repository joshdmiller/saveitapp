package models

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection

import play.api.Play
import play.api.libs.json._

case class Item (@Key("_id") id: ObjectId, title: String, user_id: ObjectId)

object ItemDAO extends SalatDAO[Item, ObjectId](collection = MongoConnection()(
  Play.current.configuration.getString("mongo.db").get
)("items")) {

  def by_id(id: String) = {
    val oid: ObjectId = new ObjectId(id)
    ItemDAO.findOne(MongoDBObject("_id" -> oid))
  }
  
  def to_jsobject(item: Item) = {
    val user = UserDAO.by_id(item.user_id).get
    
    JsObject(
      "id" -> JsString("/users/"+user.username+"/items/"+item.id) ::
      "user" -> UserDAO.to_jsobject(user) ::
      Nil
    )
  }

}

