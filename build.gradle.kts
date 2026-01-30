plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    id("org.jetbrains.kotlin.plugin.spring") version "1.7.21"
    id("jacoco")
    id("org.jlleitschuh.gradle.ktlint") version "11.4.0"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    checkstyle
}

group = "moomark"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.jar {
    enabled = false
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.named("check") {
    dependsOn(tasks.named("ktlintCheck"))
}

checkstyle {
    toolVersion = "8.26"
    configFile = file("./checkstyle.xml")
    reportsDir = file("$buildDir/checkstyle-output")
}

tasks.checkstyleMain {
    reports {
        xml.outputLocation.set(layout.buildDirectory.file("checkstyle-output/checkstyle-report.xml"))
    }
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    debug.set(false)
    verbose.set(true)
    android.set(false) // Android 프로젝트가 아니면 false
    outputToConsole.set(true)
    ignoreFailures.set(false) // 린트 에러 발생 시 빌드 실패 처리 (권장)
    enableExperimentalRules.set(true) // 최신 규칙 적용 여부
    filter {
        exclude("**/generated/**") // 생성된 파일 제외
    }
}
