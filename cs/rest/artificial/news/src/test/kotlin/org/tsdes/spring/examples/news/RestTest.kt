package org.tsdes.spring.examples.news

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import org.tsdes.spring.examples.news.api.V2_NEWS_JSON
import org.tsdes.spring.examples.news.dto.NewsDto

/**
 * Created by arcuri82 on 14-Jul-17.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestTest {

    @LocalServerPort
    protected var port = 0

    @Before
    @After
    fun clean() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/news"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        /*
           Here, we read each resource (GET), and then delete them
           one by one (DELETE)
         */
        val list = given().accept(ContentType.JSON).get()
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<NewsDto>::class.java)
                .toList()


        /*
            Code 204: "No Content". The server has successfully processed the request,
            but the return HTTP response will have no body.
         */
        list.stream().forEach {
            given().pathParam("id", it.newsId)
                    .delete("/{id}")
                    .then()
                    .statusCode(204)
        }

        given().get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(0))
    }


    @Test
    fun testCreateAndGetWithNewFormat() {

        val author = "author"
        val text = "someText"
        val country = "Norway"
        val dto = NewsDto(null, author, text, country, null)

        //no news
        given().contentType(V2_NEWS_JSON)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(0))

        //create a news
        val id = given().contentType(V2_NEWS_JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)
                .extract().asString()

        //should be 1 news now
        given().contentType(V2_NEWS_JSON)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))

        //1 news with same data as the POST
        given().accept(V2_NEWS_JSON)
                .pathParam("id", id)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("newsId", equalTo(id))
                .body("authorId", equalTo(author))
                .body("text", equalTo(text))
                .body("country", equalTo(country))
    }

    @Test
    fun testDoubleDelete() {

        val dto = NewsDto(null, "author", "text", "Norway", null)

        //create a news
        val id = given().contentType(V2_NEWS_JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)
                .extract().asString()

        RestAssured.delete("/" + id).then().statusCode(204)

        //delete again
        RestAssured.delete("/" + id).then().statusCode(404) //note the change from 204 to 404
    }


    @Test
    fun testWithRestAssured() {


        given().accept(ContentType.JSON)
                .and()
                .get("http://localhost:$port/countries")
                .then()
                .statusCode(200)
                .and()
                .body("size()", Matchers.greaterThan(200))
                .body(Matchers.containsString("Norway"))
                .body(Matchers.containsString("Sweden"))
                .body(Matchers.containsString("Germany"))
    }

    @Test
    fun testSwaggerSchema(){

        RestAssured.get("../v2/api-docs")
                .then()
                .statusCode(200)
                .body("swagger", equalTo("2.0"))
    }

    @Test
    fun testSwaggerUI(){
        RestAssured.get("../swagger-ui.html").then().statusCode(200)
    }
}