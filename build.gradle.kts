plugins {
    java
    id("org.cadixdev.licenser") version "0.5.0"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.cadixdev.licenser")
    
    group = "com.wholegrain-software"

    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    license {
        header = rootProject.file("LICENSE")
    }
}



