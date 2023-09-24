
repositories {
    mavenLocal()
    mavenCentral()
    maven( url ="https://jcenter.bintray.com")
}


plugins {
    `java-library`
 //   id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


//dependencyManagement  {
//    imports {
//   //     mavenBom("io.micronaut:micronaut-bom:1.3.4")
//    }
//}

val EVOMASTER_VERSION = project.ext.get("EVOMASTER_VERSION")

dependencies{
    implementation("org.evomaster:evomaster-client-java-controller:$EVOMASTER_VERSION")
    implementation("org.evomaster:evomaster-client-java-dependencies:$EVOMASTER_VERSION")

    api(project(":cs:rest:reservations-api"))

    //Gradle api() is not importing transitive dependencies???
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.0")
    implementation("org.mongodb:mongodb-driver-sync:4.4.2")
}