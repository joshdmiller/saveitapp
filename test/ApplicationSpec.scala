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

    // A placeholder for a created item's id
    var item_uri  = ""

    /* BASIC TESTS */
    
    "render the index page" in {
      val home = routeAndCall(FakeRequest(GET, "/")).get
      
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Save It App Home")
    }

    "send 404 on a bad request" in {
      running(server) {
        val apiCall = await(WS.url("http://localhost:3333/blah").get)
        
        apiCall.status mustEqual NOT_FOUND
        apiCall.getAHCResponse.getContentType must contain("application/json")
      
        val json = apiCall.json
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual NOT_FOUND
      }
    }

    /* NON-EXISTENT USER */
    
    "returns an error for a nonexistent user: GET /users/"+username in {
      running(server) {
        val response = await(WS.url("http://localhost:3333/users/"+username).get)
        response must not be none

        response.status mustEqual NOT_FOUND
        response.getAHCResponse.getContentType must contain("application/json")
        
        val json = Json.parse(response.body)
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual NOT_FOUND
      }
    }

    // TODO: nonexistent user's items returns 404
    "returns 404 for a nonexistent user's items: GET /users/"+username+"/items" in {
      running(server) {
        val response = await(WS.url("http://localhost:3333/users/"+username+"/items").get)
        response must not be none

        response.status mustEqual NOT_FOUND
        response.getAHCResponse.getContentType must contain("application/json")
        
        val json = Json.parse(response.body)
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual NOT_FOUND
      }
    }

    /* USER RESOURCE */

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

    "return JSON from API call: GET /users/"+username in {
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

    /* ITEM RESOURCE */

    "returns an empty list of items for a new user: GET /users/"+username+"/items" in {
      running(server) {
        val response = await(WS.url("http://localhost:3333/users/"+username+"/items").get)
        response must not be none
        
        val json = Json.parse(response.body)
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual OK
        
        // ensure we have an id
        val userid = (json \ "user" \ "id").asOpt[String]
        userid must not be none
        userid.get mustEqual "/users/"+username

        // ensure we have a list of items
        val items = (json \ "items").asOpt[List[String]]
        items must not be none
        items.get.length mustEqual 0
      }
    }

    "can create an item for a user: POST /users/"+username+"/items" in {
      running(server) {
        val body = JsObject(
          "title" -> JsString("test-item") :: 
          Nil
        )

        val response = await(WS
          .url("http://localhost:3333/users/"+username+"/items")
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

        val oid = (json \ "id").asOpt[String]
        oid must not be none
        item_uri = oid.get
        success
      }
    }

    "returns a list of items for a user: GET /users/"+username+"/items" in {
      running(server) {
        val response = await(WS.url("http://localhost:3333/users/"+username+"/items").get)
        response must not be none
        
        // ensure we have a list of items
        val json = Json.parse(response.body)
        val items = (json \ "items").asOpt[List[String]]
        items must not be none
        items.get.length mustEqual 1
        items.get.contains(item_uri) must beTrue
      }
    }

    "returns a particular item: GET "+item_uri in {
      running(server) {
        val response = await(WS.url("http://localhost:3333"+item_uri).get)
        response must not be none
        
        val json = Json.parse(response.body)  
        val code = (json \ "status").asOpt[Int]
        code must not be none
        code.get mustEqual OK
        
        // ensure we have a user id
        val userid = (json \ "user" \ "id").asOpt[String]
        userid must not be none
        userid.get mustEqual "/users/"+username

        // ensure we have an item
        val id = (json \ "id").asOpt[String]
        id must not be none
        id.get mustEqual item_uri
      }
    }
  }
}
