import com.google.protobuf.gradle.proto

plugins {
    java
    kotlin("jvm") version "1.4.10"
    id("com.google.protobuf") version "0.8.13"
}

group = "com.nnt"
version = "1.0-SNAPSHOT"

repositories {
    maven { setUrl("https://maven.aliyun.com/repository/central") }
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

// dubbo
    implementation("org.apache.dubbo:dubbo:2.7.8")
    implementation("org.apache.dubbo:dubbo-dependencies-zookeeper:2.7.8")

// grpc
    implementation("com.google.protobuf:protobuf-java:3.6.1")
    implementation("io.grpc:grpc-stub:1.15.1")
    implementation("io.grpc:grpc-protobuf:1.15.1")
    runtimeOnly("com.google.protobuf:protobuf-gradle-plugin:0.8.13")

// test
    testImplementation("junit:junit:4.12")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.6.1"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}
