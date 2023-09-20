
repositories {
    mavenLocal()
    mavenCentral()
    maven( url ="https://jcenter.bintray.com")
}


plugins {
    `java-library`
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


val EVOMASTER_VERSION = "1.6.2-SNAPSHOT"

dependencies{
    implementation("org.evomaster:evomaster-client-java-controller:$EVOMASTER_VERSION")
    implementation("org.evomaster:evomaster-client-java-dependencies:$EVOMASTER_VERSION")

    api(project(":cs:rest:bibliothek"))

    //Gradle api() is not importing transitive dependencies???
//    implementation("org.springframework.boot:spring-boot-starter-web:2.7.0")
//    implementation("org.mongodb:mongodb-driver-sync:4.4.2")
}