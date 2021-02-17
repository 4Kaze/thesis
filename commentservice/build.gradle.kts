import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
    kotlin("plugin.jpa") version "1.4.21"
}

group = "pl.lodz.p.stanczyk"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(platform("org.keycloak.bom:keycloak-adapter-bom:6.0.1"))
    implementation("org.keycloak:keycloak-spring-boot-starter")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.jsonwebtoken:jjwt-api:0.11.2")
    testImplementation("io.jsonwebtoken:jjwt-impl:0.11.2")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    testImplementation("org.codehaus.groovy:groovy:3.0.7")
    testImplementation("org.codehaus.groovy:groovy-json:3.0.7")
    testImplementation(platform("org.spockframework:spock-bom:2.0-M4-groovy-3.0"))
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-spring")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.27.2")
    testImplementation("org.bitbucket.b_c:jose4j:0.7.3")
}

sourceSets {
    create("integration") {
        withConvention(GroovySourceSet::class) {
            groovy {
                setSrcDirs(listOf("$projectDir/src/integration/groovy"))
                compileClasspath += sourceSets["main"].output
                runtimeClasspath += sourceSets["main"].output
            }
        }
    }
}

configurations {
    getByName("integrationImplementation").extendsFrom(testImplementation.get())
    getByName("integrationRuntime").extendsFrom(testRuntime.get())
}

tasks {
    val integrationTest by registering(Test::class) {
        named("integrationTest")
        description = "Run integration tests"
        group = "verification"
        testClassesDirs = sourceSets["integration"].output.classesDirs
        classpath = sourceSets["integration"].runtimeClasspath
    }

    check {
        dependsOn(integrationTest)
    }

    getByName("ktlintCheck") {
        dependsOn(getByName("ktlintFormat"))
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
