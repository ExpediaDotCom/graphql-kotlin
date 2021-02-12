description = "GraphQL Kotlin common utilities to generate a client."

val graphQLJavaVersion: String by project
val kotlinPoetVersion: String by project
val kotlinxSerializationVersion: String by project
val ktorVersion: String by project
val wireMockVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api("com.graphql-java:graphql-java:$graphQLJavaVersion") {
        exclude(group = "com.graphql-java", module = "java-dataloader")
    }
    api("com.squareup:kotlinpoet:$kotlinPoetVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wireMockVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.90".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.75".toBigDecimal()
                }
            }
        }
    }
}
