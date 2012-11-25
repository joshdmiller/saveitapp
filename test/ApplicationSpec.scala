package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {
  
  "Application" should {
    
    "send 404 on a bad request" in {
      running(FakeApplication()) {
        routeAndCall(FakeRequest(GET, "/boum")) must beNone
      }
    }
    
    "render the index page" in {
      running(FakeApplication()) {
        val home = routeAndCall(FakeRequest(GET, "/")).get
        
        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain ("Save It App Home")
      }
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
      val id = (json \ "user" \ "id").asOpt[Int]
      id must not be none
      id.get mustEqual 1234
    }
  }
}
