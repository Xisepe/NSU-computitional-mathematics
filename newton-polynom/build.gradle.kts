import org.gradle.internal.impldep.com.fasterxml.jackson.core.JsonPointer.compile

plugins {
    id("java")
}

group = "ru.ccfit.golubevm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.jfree/jfreechart
    implementation("org.jfree:jfreechart:1.5.4")

}

tasks.test {
    useJUnitPlatform()
}