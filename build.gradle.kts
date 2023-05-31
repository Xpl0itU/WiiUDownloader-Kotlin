plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "org.xpl0itu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.8.5")
}