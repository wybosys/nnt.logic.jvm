import com.google.protobuf.gradle.*

plugins {
    java
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
    id("com.google.protobuf") version "0.8.13"
}

group = "com.nnt"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/central")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
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

        kotlin {

        }

        java {
            srcDir("src/main")
        }
    }
}

dependencies {

    // kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9-native-mt")

    // logic
    implementation("com.google.auto.service:auto-service:1.0-rc7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.9.3")

    // grpc
    implementation("com.google.protobuf:protobuf-java:3.12.0")

    // dubbo
    implementation("com.alibaba:dubbo:2.6.9")
    implementation("org.apache.dubbo:dubbo-dependencies-zookeeper:2.7.8")
    implementation("org.apache.curator:curator-framework:5.1.0")

    // dubbo-rest
    implementation("io.netty:netty-all:4.1.9.Final")
    implementation("org.jboss.resteasy:resteasy-jaxrs:3.9.0.Final")
    implementation("org.jboss.resteasy:resteasy-client:3.9.0.Final")
    implementation("org.jboss.resteasy:resteasy-netty4:3.9.0.Final")
    implementation("org.jboss.resteasy:resteasy-jackson-provider:3.9.0.Final")
    implementation("org.jboss.resteasy:resteasy-jaxb-provider:3.9.0.Final")

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