[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

# session-service

RESTful API to cBioPortal/cbioportal sessions in MongoDB.  

Session information is stored in JSON, so this API generalizes to any JSON objects.


## Run with Docker
```
docker-compose up
```
You can also run it in detached mode with:
```
docker-compose up -d
```
In that case, to stop it one runs:
```
docker-compose down
```
If you want to rebuild the session service image after having made changes in
development:
```
docker-compose up --build
```
Check http://localhost:8080/info to confirm session service is running. It
should show a version number.

Test whether a session can be created like this:

```
curl -H "Content-Type: application/json" --user user:pass -X POST http://localhost:8080/api/sessions/test_portal/main_session --data '{"title": "my main portal session", "description": "this is an example"}'
```

The mongo database port is not exposed by default. One can connect to the
mongo database like this:
```
docker exec -it session-service_db_1 mongo mongodb://localhost:27017
```



## Run without docker
### Requirements

JDK 1.7 or later: http://www.oracle.com/technetwork/java/javase/downloads/index.html

Maven 3.0+: http://maven.apache.org/download.cgi

MongoDB: https://docs.mongodb.org/manual/

### Installation and setup

Create database 'session_service' using the 'mongo' shell interface to MongoDB:

```
$ mongo

> use session_service
```
Clone repository, compile, run tests, and start server:
```
$ git clone https://github.com/cBioPortal/session-service.git

$ cd session-service

session-service$ mvn package -Dpackaging.type=jar && java -Dspring.data.mongodb.uri=mongodb://localhost:27017/session-service -jar target/session_service-0.1.0.jar 
```
To generate war file with a configuration file:

```
$ cd session-service

session-service$ mkdir src/main/resources/

session-service$ cp /path/to/myapp.config src/main/resources/

session-service$ mvn package
```

An example properties file:

```
spring.data.mongodb.database=session_service
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
```

To have a context root that is not "/", add to the properties file:

```
server.contextPath=/session_service
server.port=8080 # change this if u want to use a different port
```

One can use session-service with or without basic authentication. It's
disabled by default. To enable basic authentication set:

```
security.basic.enabled=true
spring.security.user.name=user
spring.security.user.password=pass
```

By default the server runs on port 8080. This can be overridden by setting the
process's SERVER_PORT environment variable.

```
session-service$ export set SERVER_PORT=8090; mvn package -Dpackaging.type=jar && java -Dspring.data.mongodb.uri=mongodb://localhost:27017/session-service -jar target/session_service-0.1.0.jar
```

## Sentry support

Sentry is already included as a dependency of this project, one can add the following this [Sentry official documentation](https://docs.sentry.io/platforms/java/guides/spring-boot/configuration/#setting-the-dsn) to enable sentry.
For example, for Run without docker, add the following when starting the server:
```
-Dsentry.dsn=https://examplePublicKey@o0.ingest.sentry.io/0
```
And example command for Run with docker also included in the docker-compoase.yaml file, please refer that command to enable sentry.

## API

Swagger documentation will be found here: http://[url]:[port]/swagger-ui.html e.g. http://localhost:8090/swagger-ui.html

If basic auth is enabled note that one should pass the username password to access these endpoints e.g. with curl:

```
curl --user user:pass
```

### Create

### Valid Type

| Type  | Description  |
|---|---|
| main_session | represent result's page query |
| virtual_study | represent a subset for samples saved from study summary page |
| group | similar to virtual study except its used in comparison page |
| comparison_session | represent comparison page query |
| settings | represent cbio page settings. page type is identified by a field `page` in it |
| custom_data | holds study-view page custom charts data |
| genomic_chart | represents genomic chart added by user in study-view page |
| custom_gene_list | represents custom gene list added by user in query page |

#### POST http://localhost:8080/api/sessions/{source}/{type}/
Creates a session.  Returns status 200 and the session id in response body
on success.  The session is saved in a collection named {type}. Both
source and type are saved in the session document. If a session with the same source, type,
and data already exists in the database returns the session id of that session
instead of creating a duplicate.  

WARNING: This is case sensitive. You should always use the same case
for source and type.

Example body for POST http://localhost:8080/api/sessions/msk_portal/main_session/
```
{"title": "my main portal session", "description": "this is an example"}
```
Example response:
```
{
  "id": "57167a52ef86d81afb415aba"
}
```
If no JSON data passed in request body or an invalid type is sent returns 400 status
with something like the following in the body:
```
{
  "timestamp": 1461093154793,
  "status": 400,
  "error": "Bad Request",
  "exception": "org.springframework.web.method.annotation.MethodArgumentTypeMismatchException",
  "message": "valid types are: main_session, virtual_study, group, comparison_session, custom_data, genomic_chart",
  "path": "/api/sessions/msk_portal/invalid_type/"
}
```
Sending invalid JSON in the request body returns a 400 status
with something like the following in the body:
```
{
  "timestamp": 1461090997119,
  "status": 400,
  "error": "Bad Request",
  "exception": "org.cbioportal.session_service.domain.exception.SessionInvalidException",
  "message": "\n{\"portal-session\": blah blah blah}\n                   ^",
  "path": "/api/sessions/msk_portal/main_session/"
}
```

### Read

#### GET http://localhost:8080/api/sessions/{source}/{type}/
Returns all sessions for source and type.  Returns "[]" if no sessions.  
Example response for GET http://localhost:8080/api/sessions/msk_portal/main_session/
```
[
  {
    "id": "57167a52ef86d81afb415aba",
    "data": {
      "title": "my main portal session",
      "description": "this is an example"
    },
    "source": "msk_portal",
    "type": "main_session"
  },
  {
    "id": "57167c69ef86fdfcec850342",
    "data": {
      "title": "my main portal session",
      "description": "this is another example"
    },
    "source": "msk_portal",
    "type": "main_session"
  }
]
```

#### GET http://localhost:8080/api/sessions/{source}/{type}/{id}
Returns single session given source, type, and id.
Example response for GET http://localhost:8080/api/sessions/msk_portal/main_session/57167a52ef86d81afb415aba
```
{
  "id": "57167a52ef86d81afb415aba",
  "data": {
    "title": "my main portal session",
    "description": "this is an example"
  },
  "source": "msk_portal",
  "type": "main_session"
}
```
If no session is found returns status 404 with a request body like this:
```
{
  "timestamp": 1462379207301,
  "status": 404,
  "error": "Not Found",
  "exception": "org.cbioportal.session_service.service.exception.SessionNotFoundException",
  "message": "Session not found",
  "path": "/api/sessions/msk_portal/main_session/test"
}
```
WARNING: This is case sensitive.
GET http://localhost:8080/api/sessions/MSK_portal/main_session/57167a52ef86d81afb415aba
and
GET http://localhost:8080/api/sessions/msk_portal/Main_Session/57167a52ef86d81afb415aba 
are NOT equivalent.

#### GET http://localhost:8080/api/sessions/{source}/{type}/query?field={field}&value={value}
Returns all sessions matching a query for source and type. Returns
200 status on success.
Example response for GET http://localhost:8080/api/sessions/msk_portal/main_session/query?field=data.title&value=my%20main%20portal%20session
```
[
  {
    "id": "57167c69ef86fdfcec850342",
    "data": {
      "title": "my main portal session",
      "description": "this is another example"
    },
    "source": "msk_portal",
    "type": "main_session"
  }
]
```

### Update

#### PUT http://localhost:8080/api/sessions/{source}/{type}/{id}
Updates a session given the source, type, and id.  Returns status 200
on success with empty request body. 
Example body for PUT http://localhost:8080/api/sessions/msk_portal/main_session/57167a52ef86d81afb415aba
```
{
    "title": "my UPDATED main portal session",
    "description": "this is an example"
}
```
If no JSON data passed in request body returns status 400 with a request
body like this:
```
{
  "timestamp": 1461092375741,
  "status": 400,
  "error": "Bad Request",
  "exception": "org.springframework.http.converter.HttpMessageNotReadableException",
  "message": "Required request body is missing: public void org.cbioportal.session_service.web.SessionServiceController.updateSession(java.lang.String,java.lang.String,java.lang.String,java.lang.String)",
  "path": "/api/sessions/msk_portal/main_session/57167a52ef86d81afb415aba"
}
```
If an invalid id is passed returns status 404 with a request body like this:
```
{
  "timestamp": 1462379078498,
  "status": 404,
  "error": "Not Found",
  "exception": "org.cbioportal.session_service.service.exception.SessionNotFoundException",
  "message": "Session not found",
  "path": "/api/sessions/msk_portal/main_session/test"
}
```
Sending invalid JSON in the request body returns a 400 status
with something like the following in the body:
```
{
  "timestamp": 1461092440979,
  "status": 400,
  "error": "Bad Request",
  "exception": "org.cbioportal.session_service.domain.exception.SessionInvalidException",
  "message": "\n{\n    \"title\": \"my UPDATED main portal session\",\n    \"description\": blah blah blah\n}\n                                                                    ^",
  "path": "/api/sessions/msk_portal/main_session/57167a52ef86d81afb415aba"
}
```

### Delete

#### DELETE http://localhost:8080/api/sessions/{source}/{type}/{id}
Deletes a session with source, type, and id.
Returns 200 status on success with empty request body. 
Example URL for DELETE http://localhost:8080/api/sessions/msk_portal/main_session/57167c69ef86fdfcec850342

If an invalid id is passed returns status 404 with a request body like this:
```
{
  "timestamp": 1462379146105,
  "status": 404,
  "error": "Not Found",
  "exception": "org.cbioportal.session_service.service.exception.SessionNotFoundException",
  "message": "Session not found",
  "path": "/api/sessions/msk_portal/main_session/test"
}
```
