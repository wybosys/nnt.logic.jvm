import com.google.protobuf.gradle.proto

plugins {
    java
    kotlin("jvm") version "1.4.10"
    id("com.google.protobuf") version "0.8.13"
}

group = "com.nnt"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
    }
    dependencies {
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.13")
    }
}

repositories {
    maven("https://maven.aliyun.com/repository/central")
}

sourceSets {
    create("logic") {
        proto {
            srcDir("src/proto")
        }
    }
}

dependencies {

    // kotlin
    implementation(kotlin("stdlib"))

    // grpc
    implementation("com.google.protobuf:protobuf-java:3.6.1")
    implementation("io.grpc:grpc-stub:1.15.1")
    implementation("io.grpc:grpc-protobuf:1.15.1")
    runtimeOnly("com.google.protobuf:protobuf-gradle-plugin:0.8.13")

    // dubbo
    implementation("org.apache.dubbo:dubbo:2.7.8")
    implementation("org.apache.dubbo:dubbo-dependencies-zookeeper:2.7.8")

    // test
    testImplementation("junit:junit:4.12")
}

protobuf {
    
}