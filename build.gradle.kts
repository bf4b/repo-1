import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.1.8.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	id("idea")
	kotlin("jvm") version "1.2.71"
	kotlin("plugin.spring") version "1.2.71"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly = configurations.create("developmentOnly")
configurations {
	developmentOnly
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

// https://docs.gradle.org/5.2.1/userguide/java_testing.html#sec:configuring_java_integration_tests
sourceSets {
	create("componentTest") {
		compileClasspath += sourceSets.main.get().output
		runtimeClasspath += sourceSets.main.get().output
	}
}

val componentTestImplementation by configurations.getting {
	extendsFrom(configurations.implementation.get())
}

configurations["componentTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

val componentTest = task<Test>("componentTest") {
	description = "Runs component-level tests."
	group = "verification"

	testClassesDirs = sourceSets["componentTest"].output.classesDirs
	classpath = sourceSets["componentTest"].runtimeClasspath
	shouldRunAfter("test")
}

tasks.check { dependsOn(componentTest) }

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

	componentTestImplementation("org.springframework.boot:spring-boot-starter-test")
	componentTestImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:2.1.2.RELEASE")
	componentTestImplementation("org.assertj:assertj-core:3.13.2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

idea {
	module {
		testSourceDirs = testSourceDirs + sourceSets["componentTest"].allJava.srcDirs
		testResourceDirs = testResourceDirs + sourceSets["componentTest"].resources.srcDirs
	}
}