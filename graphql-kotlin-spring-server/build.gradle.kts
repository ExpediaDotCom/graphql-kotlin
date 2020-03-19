description = "Spring Boot autoconfiguration library for creating reactive GraphQL server"

plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
}

val kotlinCoroutinesVersion: String by project
val springBootVersion: String by project
val reactorVersion: String by project
val reactorExtensionsVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-federation"))
    api("org.springframework.boot:spring-boot-starter-webflux:$springBootVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
    kapt("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorExtensionsVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.96".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.91".toBigDecimal()
                }
            }
        }
    }
}
