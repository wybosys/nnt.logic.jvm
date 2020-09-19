import com.google.protobuf.gradle.*

plugins {
    java
    kotlin("jvm") version "1.4.10"
    id("com.google.protobuf") version "0.8.13"
}

group = "com.nnt"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/central")
    }
    dependencies {
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.13")
    }
}

repositories {
    maven("https://maven.aliyun.com/repository/central")
}

sourceSets {
    main {
        proto {
            srcDir("src/proto")
        }
    }
}

dependencies {

    // kotlin
    implementation(kotlin("stdlib"))

    // grpc
    implementation("com.google.protobuf:protobuf-java:3.12.0")
    implementation("io.grpc:grpc-stub:1.32.1")
    implementation("io.grpc:grpc-protobuf:1.32.1")

    // dubbo
    implementation("org.apache.dubbo:dubbo:2.7.8")
    implementation("org.apache.dubbo:dubbo-dependencies-zookeeper:2.7.8")

    // test
    testImplementation("junit:junit:4.12")
}

protobuf {

    protoc {
        artifact = "com.google.protobuf:protoc:3.0.0"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.30.2"
        }
    }
}