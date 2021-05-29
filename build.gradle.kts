import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
}

group = "com.reddit.aroundtheworldmc"
version = "3.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    implementation("com.destroystokyo.paper:paper-api:1.16.3-R0.1-SNAPSHOT")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    project.gradle.addBuildListener(object : BuildListener {
        override fun buildStarted(gradle: Gradle) {}

        override fun settingsEvaluated(settings: Settings) {}

        override fun projectsLoaded(gradle: Gradle) {}

        override fun projectsEvaluated(gradle: Gradle) {}

        override fun buildFinished(result: BuildResult) {
            copy {
                from("build/libs/LLChat-3.0.1.jar")
                into("papermc/plugins")
            }
        }

    })
}
