import org.gradle.jvm.tasks.Jar


repositories {
    mavenLocal()
    mavenCentral()
    maven( url ="https://jcenter.bintray.com")
}


plugins {
    `java-library`
    application
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
    implementation("org.evomaster:evomaster-client-java-instrumentation:$EVOMASTER_VERSION")
    implementation("org.evomaster:evomaster-client-java-dependencies:$EVOMASTER_VERSION")
    implementation("org.mongodb:mongodb-driver-sync:4.9.1")
}



val fatJar = task("fatJar", type = Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.WARN
    archiveBaseName.set("${project.name}-evomaster-runner")
    isZip64 = true
    manifest {
        attributes["Implementation-Title"] = "EM"
        attributes["Implementation-Version"] = "1.0"
        attributes["Main-Class"] = "em.external.bibliothek.ExternalEvoMasterController"
        attributes["Premain-Class"] = "org.evomaster.client.java.instrumentation.InstrumentingAgent"
        attributes["Agent-Class"] = "org.evomaster.client.java.instrumentation.InstrumentingAgent"
        attributes["Can-Redefine-Classes"] = "true"
        attributes["Can-Retransform-Classes"] = "true"
    }
    from(configurations.runtimeClasspath.get().map{ if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
