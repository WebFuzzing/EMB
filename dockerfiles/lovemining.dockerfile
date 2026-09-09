FROM amazoncorretto:21-alpine-jdk

COPY ./dist/lovemining-sut.jar .
COPY ./dist/jacocoagent.jar .




ENTRYPOINT \
    java \
    -javaagent:jacocoagent.jar=output=tcpserver,address=*,port=6300,append=false,dumponexit=false \
     -jar lovemining-sut.jar \
    --server.port=8080 --spring.data.mongodb.uri=mongodb://db:27017/LoveMining --spring.neo4j.uri=bolt://neo4j:7687 --spring.neo4j.authentication.username=neo4j --spring.neo4j.authentication.password=wfdNeo4jPass123