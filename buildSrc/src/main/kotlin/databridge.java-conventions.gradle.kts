// Convenciones comunes a todos los módulos Java de DataBridge.
plugins {
    `java-library`
    id("com.diffplug.spotless")
}

val libs = versionCatalogs.named("libs")

group = "com.openfactu.databridge"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.findVersion("java").get().requiredVersion)
    }
}

dependencies {
    val bom = platform(libs.findLibrary("spring-boot-bom").get())
    implementation(bom)
    testImplementation(bom)
    testImplementation(libs.findLibrary("junit-jupiter").get())
    testImplementation(libs.findLibrary("assertj-core").get())
    testRuntimeOnly(libs.findLibrary("junit-platform-launcher").get())
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // Todos los avisos activos y tratados como errores; -parameters conserva
    // los nombres de parámetros para Jackson/Spring.
    options.compilerArgs.addAll(listOf("-Xlint:all,-processing", "-Werror", "-parameters"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("failed", "skipped")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

spotless {
    java {
        target("src/**/*.java")
        palantirJavaFormat(libs.findVersion("palantir-java-format").get().requiredVersion)
        trimTrailingWhitespace()
        endWithNewline()
    }
}
