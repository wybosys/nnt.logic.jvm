plugins {
    java
    idea
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
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

                exclude("com/nnt/flink")
                exclude("com/test/flink")
            }
        }

        resources {
            srcDir("../src/main/resources")
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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.0")
    implementation("com.ctrip.framework.apollo:apollo-client:1.7.0")

    // db
    implementation("org.mybatis:mybatis:3.5.5")
    implementation("com.alibaba:druid:1.1.24")
    implementation("mysql:mysql-connector-java:8.0.21")
    implementation("redis.clients:jedis:3.3.0")
    implementation("org.neo4j.driver:neo4j-java-driver:4.1.1")
    implementation("org.apache.hbase:hbase-client:2.2.6")
    implementation("org.apache.phoenix:phoenix-queryserver-client:5.0.0-HBase-2.0")
    implementation("org.springframework:spring-jdbc:4.3.8.RELEASE")

    // mq
    implementation("org.apache.kafka:kafka-clients:2.6.0")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}
