plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.detekt)
    application
}

group = "com.tonihacks"
version = "1.0"

repositories {
    mavenCentral()
}

// Java and Kotlin configuration
kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Dependencies configuration grouped by purpose
dependencies {
    // SBOM dependencies
    implementation(platform(libs.aws.bom))

    // Core dependencies
    implementation(libs.kotlin.stdlib)
    implementation(libs.aws.bedrock.runtime)

    // Logging
    implementation(libs.logging)
    implementation(libs.logback.classic)
}

application {
    mainClass.set("com.tonihacks.MainKt")
}

// Task configurations
tasks {
        // JAR configuration
    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "com.tonihacks.MainKt"
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    // Shadow JAR configuration
    shadowJar {
        archiveBaseName.set("bedrock-helloworld")
        archiveClassifier.set("")
        archiveVersion.set("")
        mergeServiceFiles()
    }

    // Build configuration
    build {
        dependsOn(shadowJar)
    }

    // Code formatting
    named("check") {
        dependsOn("ktfmtFormat")
        dependsOn("detekt")
    }

    // Disable unnecessary Java compilation
    named("compileJava") {
        enabled = false
    }
    named("compileTestJava") {
        enabled = false
    }
}

detekt {
    parallel = true
}

// Code style configuration
ktfmt {
    kotlinLangStyle()
}
