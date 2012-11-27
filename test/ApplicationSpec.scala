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
    val app      = FakeApplication()
    val server   = TestServer(3333)

    // A user with which to test
    val rand     = new scala.util.Random
    val username = "testuser-"+rand.nextInt(1000)
    
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

    "can create a user: POST /users" in {
      running(server) {
        val body = JsObject(
          "username" -> JsString(username) :: 
          Nil
        )

        val response = await(WS
          .url("http://localhost:3333/users")
          .withHeaders("Content-Type" -> "application/json")
          .post(body)
        )

        response must not be none
        response.status mustEqual OK
        response.getAHCResponse.getContentType must contain("application/json")
        
        val json = Json.parse(response.body)
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual OK
      }
    }

    "return JSON from API call: GET /users/{id}" in {
      running(server) {
        val response = await(WS.url("http://localhost:3333/users/"+username).get)
        response must not be none

        response.status mustEqual OK
        response.getAHCResponse.getContentType must contain("application/json")
        
        val json = Json.parse(response.body)
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual OK
        
        // ensure we have an id
        val id = (json \ "id").asOpt[String]
        id must not be none
        id.get mustEqual "/users/"+username
      }
    }

    "returns an error for a nonexistent user: GET /users/{id}" in {
      running(FakeApplication()) {
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
    }

    "returns a list of items for a user: GET /users/{id}/items" in {
      running(FakeApplication()) {
        val result = routeAndCall(FakeRequest(GET, "/users/joshdmiller/items")).get
        val json = Json.parse(contentAsString(result))
        
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual OK
        
        // ensure we have an id
        val userid = (json \ "user" \ "id").asOpt[String]
        userid must not be none
        userid.get mustEqual "/users/joshdmiller"

        // ensure we have a list of items
        val items = (json \ "items").asOpt[List[String]]
        items must not be none
        items.get.length mustEqual 3
      }
    }

    "returns a particular article: GET /users/{id}/items/{id}" in {
      running(FakeApplication()) {
        val list_result = routeAndCall(FakeRequest(GET, "/users/joshdmiller/items")).get
        val list_json = Json.parse(contentAsString(list_result))
        val items: List[String] = (list_json \ "items").as[List[String]]
        val item_uri = items(0)
        
        val apiCall = routeAndCall(FakeRequest(GET, item_uri))
        apiCall must not be none
        val json = Json.parse(contentAsString(apiCall.get))
        
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual OK
        
        // ensure we have an id
        val userid = (json \ "user" \ "id").asOpt[String]
        userid must not be none
        userid.get mustEqual "/users/joshdmiller"

        // ensure we have an item
        val id = (json \ "id").asOpt[String]
        id must not be none
        id.get mustEqual item_uri
      }
    }
  }
}
