plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.knowm.xchart:xchart:3.8.8")
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
}

publishing {
    repositories {
        mavenLocal()
    }
}