import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version "0.36.0"
	kotlin("jvm") version "1.4.20"
    application
}

repositories {
	mavenCentral()
	jcenter()
	google()
}

dependencies {
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.7.0")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.7.0")

    testImplementation(group = "org.mockito", name = "mockito-core", version = "3.6.0")
    testImplementation(group = "org.mockito", name = "mockito-junit-jupiter", version = "3.6.0")

    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.18.1")
}

application {
    mainClass.set("com.liquidforte.roguelike.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "14"
}