package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import play.api.libs.ws.WS

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {
  
  "Application" should {
    
    "send 404 on a bad request" in {
      running(TestServer(3333)) {
        val apiCall = await(WS.url("http://localhost:3333/blah").get)
        
        apiCall.status mustEqual NOT_FOUND
        apiCall.getAHCResponse.getContentType must contain("application/json")
      
        val json = apiCall.json
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual NOT_FOUND
      }
    }
    
    "render the index page" in {
      val home = routeAndCall(FakeRequest(GET, "/")).get
      
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Save It App Home")
    }

    "return JSON from API call: GET /users/{id}" in {
      val apiCall = routeAndCall(FakeRequest(GET, "/users/1234"))
      apiCall must not be none

      val  result = apiCall.get
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      
      val json = Json.parse(contentAsString(result))
      val code = (json \ "status").asOpt[Int]
      code must not be none
      code.get mustEqual OK
    }

    "return valid user data from API call: GET /users/{id}" in {
      val result = routeAndCall(FakeRequest(GET, "/users/1234")).get
      val json = Json.parse(contentAsString(result))
      
      // ensure we have an id
      val id = (json \ "user" \ "id").asOpt[String]
      id must not be none
      id.get mustEqual "/users/1234"
    }

    "returns an error for a nonexistent user: GET /users/{id}" in {
      val apiCall = routeAndCall(FakeRequest(GET, "/users/5678"))
      apiCall must not be none

      val  result = apiCall.get
      status(result) must equalTo(NOT_FOUND)
      contentType(result) must beSome.which(_ == "application/json")
      
      val json = Json.parse(contentAsString(result))
      val code = (json \ "status").asOpt[Int]
      code must not be none
      code.get mustEqual NOT_FOUND
    }

    "returns a list of items for a user: GET /users/{id}/items" in {
      val result = routeAndCall(FakeRequest(GET, "/users/1234/items")).get
      val json = Json.parse(contentAsString(result))
      
      val code = (json \ "status").asOpt[Int]
      code must not be none
      code.get mustEqual OK
      
      // ensure we have an id
      val userid = (json \ "user" \ "id").asOpt[String]
      userid must not be none
      userid.get mustEqual "/users/1234"

      // ensure we have a list of items
      val items = (json \ "items").asOpt[List[String]]
      items must not be none
      items.get.length mustEqual 3
    }

    "returns a particular article: GET /users/{id}/items/{id}" in {
      val apiCall = routeAndCall(FakeRequest(GET, "/users/1234/items/valid-item"))
      apiCall must not be none
      val json = Json.parse(contentAsString(apiCall.get))
      
      val code = (json \ "status").asOpt[Int]
      code must not be none
      code.get mustEqual OK
      
      // ensure we have an id
      val userid = (json \ "user" \ "id").asOpt[String]
      userid must not be none
      userid.get mustEqual "/users/1234"

      // ensure we have an item
      val id = (json \ "id").asOpt[String]
      id must not be none
      id.get mustEqual "/users/1234/items/valid-item"
    }
  }
}
