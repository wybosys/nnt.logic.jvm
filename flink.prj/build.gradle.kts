plugins {
    java
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
}

group = "com.nnt"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/central")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
    }
}

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/apache-snapshots")
}

sourceSets {
    main {
        kotlin {
            java {
                srcDir("../src/main")

                exclude("com/nnt/dubbo")
                exclude("com/test/dubbo")
            }
        }
    }

    test {
        kotlin {
            java {
                srcDir("../src/test")
            }
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
    implementation("com.ctrip.framework.apollo:apollo-client:1.7.0")

    // db
    implementation("org.mybatis:mybatis:3.5.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.3.4.RELEASE")
    implementation("mysql:mysql-connector-java:8.0.21")
    implementation("redis.clients:jedis:3.3.0")
    implementation("org.apache.hbase:hbase:2.3.2")
    implementation("org.neo4j:neo4j:4.1.2")
    implementation("org.apache.phoenix:phoenix:4.15.0-HBase-1.4")

    // grpc
    implementation("com.google.protobuf:protobuf-java:3.12.0")
    implementation("com.google.protobuf:protobuf-java-util:3.12.0")
    implementation("io.grpc:grpc-all:1.32.1")

    // flink
    implementation("com.alibaba.blink:flink-java:blink-3.7.0")
    implementation("com.alibaba.blink:flink-streaming-java_2.11:blink-3.7.0")
    implementation("com.alibaba.blink:flink-clients_2.11:blink-3.7.0")
    implementation("org.apache.flink:statefun-sdk:2.1.0")
    implementation("org.apache.flink:statefun-flink-harness:2.1.0")
    implementation("org.apache.bahir:flink-connector-redis_2.11:1.0")
    implementation("com.alibaba.blink:flink-connector-kafka-0.10_2.11:blink-3.7.0")
    implementation("com.alibaba.ververica:flink-connector-mysql-cdc:1.1.0")
    implementation("org.apache.flink:flink-connector-hbase_2.11:1.11.2")
    implementation("com.alibaba.blink:flink-connector-elasticsearch-base_2.11:blink-3.7.0")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}
