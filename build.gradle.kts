plugins {
    id("java")
    kotlin("jvm") version "2.2.0"
    application
    id("me.champeau.jmh") version "0.7.3"
}

group = "edu.adarko22"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
    jmh("org.openjdk.jmh:jmh-core:1.37")
    jmhAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")
}

tasks.test {
    useJUnitPlatform()
}

apply<edu.adarko22.jmh.CustomJmhPlugin>()
