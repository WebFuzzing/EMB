package org.tsdes.spring.examples.news.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.tsdes.spring.examples.news.db.NewsRepository
import org.tsdes.spring.examples.news.dto.NewsConverter
import org.tsdes.spring.examples.news.dto.NewsDto
import javax.validation.ConstraintViolationException

const val ID_PARAM = "The numeric id of the news"
const val BASE_JSON = "application/json;charset=UTF-8"

/*
    note the "vnd." (which starts for "vendor") and the
    "+json" (ie, treat it having JSON structure/syntax)
*/
const val V2_NEWS_JSON = "application/vnd.tsdes.news+json;charset=UTF-8;version=2"



/**
 * Created by arcuri82 on 13-Jul-17.
 */
@Api(value = "/news", description = "Handling of creating and retrieving news")
@RequestMapping(
        path = arrayOf("/news"), // when the url is "<base>/news", then this class will be used to handle it
        produces = arrayOf(
                V2_NEWS_JSON, //custom Json with versioning
                BASE_JSON //old format
        )
)
@RestController
@Validated // This is needed to do automated input validation
class NewsRestApi {


    @Autowired
    private lateinit var crud: NewsRepository

    /*
        request URL parameters are in the form

        ?<name>=<value>&<name>=<value>&...

        for example

        /news?country=Norway&authordId=foo

        So here we ll have a single endpoint for getting "news", where
        optional filtering on "country" and "authorId" will be based on
        URL parameters, and not different endpoints
     */
    @ApiOperation("Get all the news")
    @GetMapping
    fun get(@ApiParam("The country name")
            @RequestParam("country", required = false)
            country: String?,
            //
            @ApiParam("The id of the author who wrote the news")
            @RequestParam("authorId", required = false)
            authorId: String?

    ): ResponseEntity<List<NewsDto>> {

        /*
            s.isNullOrBlank() might look weird when coming from Java...
            I mean, if a string "s" is null, wouldn't calling (any) method
            on it lead to a NPE???
            This does not happen based on how kotlin code is compiled (you
            can look into the source code of isNullOrBlank to see how exactly
            this is achieved, eg by inlining and specifying the method can
            be called on nullable objects)
         */

        val list = if (country.isNullOrBlank() && authorId.isNullOrBlank()) {
            crud.findAll()
        } else if (!country.isNullOrBlank() && !authorId.isNullOrBlank()) {
            crud.findAllByCountryAndAuthorId(country!!, authorId!!)
        } else if (!country.isNullOrBlank()) {
            crud.findAllByCountry(country!!)
        } else {
            crud.findAllByAuthorId(authorId!!)
        }

        return ResponseEntity.ok(NewsConverter.transform(list))
    }


    @ApiOperation("Create a news")
    @PostMapping(consumes = arrayOf(V2_NEWS_JSON, BASE_JSON))
    @ApiResponse(code = 201, message = "The id of newly created news")
    fun createNews(
            @ApiParam("Text of news, plus author id and country. Should not specify id or creation time")
            @RequestBody
            dto: NewsDto)
            : ResponseEntity<Long> {

        if (!(dto.id.isNullOrEmpty() && dto.newsId.isNullOrEmpty())) {
            //Cannot specify id for a newly generated news
            return ResponseEntity.status(400).build()
        }

        if (dto.creationTime != null) {
            //Cannot specify creationTime for a newly generated news
            return ResponseEntity.status(400).build()
        }

        if (dto.authorId == null || dto.text == null || dto.country == null) {
            return ResponseEntity.status(400).build()
        }

        val id: Long?
        try {
            id = crud.createNews(dto.authorId!!, dto.text!!, dto.country!!)
        } catch (e: ConstraintViolationException) {
            return ResponseEntity.status(400).build()
        }

        if (id == null) {
            //this likely would happen only if bug
            return ResponseEntity.status(500).build()
        }

        return ResponseEntity.status(201).body(id)
    }

    /*
        In the following, we changed the URL from "/news/id/{id}"  to "/news/{id}"
     */


    @ApiOperation("Get a single news specified by id")
    @GetMapping(path = arrayOf("/{id}"))
    fun getNews(@ApiParam(ID_PARAM)
                @PathVariable("id")
                pathId: String?)
            : ResponseEntity<NewsDto> {

        val id: Long
        try {
            id = pathId!!.toLong()
        } catch (e: Exception) {
            /*
                invalid id. But here we return 404 instead of 400,
                as in the API we defined the id as string instead of long
             */
            return ResponseEntity.status(404).build()
        }

        val dto = crud.findById(id).orElse(null) ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(NewsConverter.transform(dto))
    }


    @ApiOperation("Update an existing news")
    @PutMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun update(
            @ApiParam(ID_PARAM)
            @PathVariable("id")
            pathId: String?,
            //
            @ApiParam("The news that will replace the old one. Cannot change its id though.")
            @RequestBody
            dto: NewsDto
    ): ResponseEntity<Any> {
        val dtoId: Long
        try {
            dtoId = getNewsId(dto)!!.toLong()
        } catch (e: Exception) {
            /*
                invalid id. But here we return 404 instead of 400,
                as in the API we defined the id as string instead of long
             */
            return ResponseEntity.status(404).build()
        }

        if (getNewsId(dto) != pathId) {
            // Not allowed to change the id of the resource (because set by the DB).
            // In this case, 409 (Conflict) sounds more appropriate than the generic 400
            return ResponseEntity.status(409).build()
        }

        if (!crud.existsById(dtoId)) {
            //Here, in this API, made the decision to not allow to create a news with PUT.
            // So, if we cannot find it, should return 404 instead of creating it
            return ResponseEntity.status(404).build()
        }

        if (dto.text == null || dto.authorId == null || dto.country == null || dto.creationTime == null) {
            return ResponseEntity.status(400).build()
        }

        try {
            crud.update(dtoId, dto.text!!, dto.authorId!!, dto.country!!, dto.creationTime!!)
        } catch (e: ConstraintViolationException) {
            return ResponseEntity.status(400).build()
        }

        return ResponseEntity.status(204).build()
    }


    @ApiOperation("Update the text content of an existing news")
    @PutMapping(path = arrayOf("/{id}/text"), consumes = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun updateText(
            @ApiParam(ID_PARAM)
            @PathVariable("id")
            id: Long?,
            //
            @ApiParam("The new text which will replace the old one")
            @RequestBody
            text: String
    ): ResponseEntity<Any> {
        if (id == null) {
            return ResponseEntity.status(400).build()
        }

        if (!crud.existsById(id)) {
            return ResponseEntity.status(404).build()
        }

        try {
            crud.updateText(id, text)
        } catch (e: ConstraintViolationException) {
            return ResponseEntity.status(400).build()
        }

        return ResponseEntity.status(204).build()
    }


    @ApiOperation("Delete a news with the given id")
    @DeleteMapping(path = arrayOf("/{id}"))
    fun delete(@ApiParam(ID_PARAM)
               @PathVariable("id")
               pathId: String?): ResponseEntity<Any> {

        val id: Long
        try {
            id = pathId!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }

        if (!crud.existsById(id)) {
            return ResponseEntity.status(404).build()
        }

        crud.deleteById(id)
        return ResponseEntity.status(204).build()
    }


    @ExceptionHandler(value = ConstraintViolationException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handleValidationFailure(ex: ConstraintViolationException): String {

        val messages = StringBuilder()

        for (violation in ex.constraintViolations) {
            messages.append(violation.message + "\n")
        }

        return messages.toString()
    }

    /**
     * Code used to keep backward compatibility
     */
    private fun getNewsId(dto: NewsDto): String? {

        if (dto.newsId != null) {
            return dto.newsId
        } else {
            return dto.id
        }
    }




}

