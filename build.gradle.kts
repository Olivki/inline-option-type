import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import name.remal.gradle_plugins.plugins.publish.bintray.RepositoryHandlerBintrayExtension
import name.remal.gradle_plugins.dsl.extensions.*

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath("name.remal:gradle-plugins:1.0.129")
    }
}

plugins {
    kotlin("jvm").version("1.3.50")

    id("com.adarshr.test-logger").version("1.6.0") // For pretty-printing for tests.
    id("com.jfrog.bintray").version("1.8.4") // For publishing to BinTray.
    id("org.jetbrains.dokka").version("0.9.17") // The KDoc engine.
    id("com.github.ben-manes.versions").version("0.20.0") // For checking for new dependency versions.

    `maven-publish`
}

apply(plugin = "name.remal.maven-publish-bintray")

project.group = "se.proxus.iadt"
project.description = "Some simple abstract data types for Kotlin, implemented as inline classes."
project.version = "1.0.0"
val repositoryName = "kotlin"
val artifactName = "inline-data-types"
val gitUrl = "URL_TO_GIT_REPO"

// General Tasks
repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    
    testImplementation(group = "io.kotlintest", name = "kotlintest-runner-junit5", version = "3.1.11")
    testImplementation(group = "org.slf4j", name = "slf4j-simple", version = "1.8.0-beta2")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.apply {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
        }
    }
}

project.afterEvaluate {
    publishing.publications.withType<MavenPublication> {
        pom {
            name.set(project.name)
            description.set(project.description)
            url.set(gitUrl)

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    email.set("oliver@berg.moe")
                    id.set("Olivki")
                    name.set("Oliver Berg")
                }
            }

            scm {
                url.set(gitUrl)
            }
        }
    }

    publishing.repositories.convention[RepositoryHandlerBintrayExtension::class.java].bintray {
        owner = "olivki"
        repositoryName = repositoryName
        packageName = artifactName
    }
}