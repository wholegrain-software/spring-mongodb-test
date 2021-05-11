plugins {
    signing
    `java-library`
    `maven-publish`
}

version = "1.2.0"

val springVersion by extra("5.3.4")
val mongoClientVersion by extra("4.1.1")
val isReleaseVersion by extra(!version.toString().endsWith("SNAPSHOT"))

dependencies {
    compileOnly("org.springframework:spring-test:${springVersion}")
    compileOnly("org.springframework:spring-context:${springVersion}")

    compileOnly("org.mongodb:bson:${mongoClientVersion}")
    compileOnly("org.mongodb:mongodb-driver-core:${mongoClientVersion}")
    compileOnly("org.mongodb:mongodb-driver-sync:${mongoClientVersion}")

    testImplementation("org.mockito:mockito-all:1.10.19")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

configurations {
    testImplementation {
        extendsFrom(compileOnly.get())
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Javadoc> {
    isFailOnError = false
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("gitlab") {
            from(components["java"])
        }

        create<MavenPublication>("library") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = version

            pom {
                name.set("Spring MongoDB Test")
                description.set("A TestExecutionListener for simplifying the database seeding in integration tests for Spring and MongoDB")
                url.set("https://github.com/Wholegrain-Software/spring-mongodb-test")
                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("DevAtHeart")
                        name.set("Jimi Steidl")
                        email.set("jimi.steidl@wholegrain-software.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Wholegrain-Software/spring-mongodb-test.git")
                    developerConnection.set("scm:git:ssh://github.com/Wholegrain-Software/spring-mongodb-test.git")
                    url.set("https://github.com/Wholegrain-Software/spring-mongodb-test")
                }

                from(components["java"])
            }
        }
    }

    repositories {
        maven {
            name = "local"
            url = uri("$buildDir/repos/release")
        }

        maven {
            name = "gitlab"
            url = uri("https://gitlab.com/api/v4/projects/25683146/packages/maven")
            credentials(HttpHeaderCredentials::class) {
                name = properties["gitlabRepositoryUser"].toString()
                value = properties["gitlabRepositoryPassword"].toString()
            }
            authentication { create<HttpHeaderAuthentication>("header") }
        }

        maven {
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"

            name = "mavenCentral"
            url = uri(if (isReleaseVersion) releasesRepoUrl else snapshotsRepoUrl)
            credentials {
                username = properties["mavenCentralUser"].toString()
                password = properties["mavenCentralPassword"].toString()
            }
        }
    }
}

signing {
    setRequired { isReleaseVersion }
    sign(publishing.publications["library"])
}
