plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "edu.metrostate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("com.google.code.gson:gson:2.13.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("edu.metrostate.MainGUI")
}