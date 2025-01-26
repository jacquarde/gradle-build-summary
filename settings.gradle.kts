rootProject.name = "gradle-build-summary"

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

dependencyResolutionManagement {
	repositories {
		mavenCentral()
	}
	versionCatalogs {
		create("main") {
			version("gradle", "8.12.1")
			version("jvm", "23")
			plugin("kotlin","org.jetbrains.kotlin.jvm").version("2.1.0")
		}
	}
}
