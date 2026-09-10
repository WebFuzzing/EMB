FROM amazoncorretto:21-alpine-jdk

COPY ./dist/adoptme-sut.jar .
COPY ./dist/jacocoagent.jar .




ENTRYPOINT \
    java \
    -javaagent:jacocoagent.jar=output=tcpserver,address=*,port=6300,append=false,dumponexit=false \
    -Xmx1G -jar adoptme-sut.jar \
    --server.port=8080 --spring.neo4j.uri=bolt://db:7687 --spring.neo4j.authentication.username=neo4j --spring.neo4j.authentication.password=neo4j123