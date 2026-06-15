plugins {
	java
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

group = "ai.gelej"
version = "0.0.1-SNAPSHOT"

val javaVersion: String by project
val springAiVersion: String by project
val telegramBotsVersion: String by project

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(javaVersion.toInt())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jspecify:jspecify")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.ai:spring-ai-starter-model-anthropic")

	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")

	implementation("org.telegram:telegrambots-client:$telegramBotsVersion")
	implementation("org.telegram:telegrambots-springboot-longpolling-starter:$telegramBotsVersion")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
