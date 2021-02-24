plugins {
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    testImplementation("com.wholegrain-software:spring-mongodb-test:1.0.0")
    testImplementation("org.testcontainers:mongodb:1.15.2")
    testImplementation("org.testcontainers:junit-jupiter:1.15.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage")
        exclude(group = "org.junit", module = "junit")
    }
}