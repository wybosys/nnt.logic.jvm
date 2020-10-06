import com.google.protobuf.gradle.*

plugins {
    java
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
    id("com.google.protobuf") version "0.8.13"
    id("org.springframework.boot") version "2.3.4.RELEASE"
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
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.13")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.3.4.RELEASE")
    }
}

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/apache-snapshots")
}

sourceSets {
    main {
        proto {
            srcDir("../src/proto/dubbo")
        }

        kotlin {
            java {
                srcDir("../src/main")
            }
        }

        resources {
            srcDir("../src/main/resources")
        }

        java {
            srcDir("build/generated/source/proto/main/java")
            srcDir("build/generated/source/proto/main/grpc-java")
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
    kapt("com.google.auto.service:auto-service:1.0-rc7")
    implementation("com.google.auto.service:auto-service-annotations:1.0-rc7")
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

    // dubbo
    implementation("org.apache.dubbo:dubbo:2.7.8")
    implementation("org.apache.dubbo:dubbo-dependencies-zookeeper:2.7.8")

    // dubbo-spring
    implementation("org.springframework:spring-remoting:2.0.8")

    // dubbo-rest
    implementation("io.netty:netty-all:4.1.9.Final")
    implementation("org.jboss.resteasy:resteasy-jaxrs:3.9.0.Final")
    implementation("org.jboss.resteasy:resteasy-client:3.9.0.Final")
    implementation("org.jboss.resteasy:resteasy-netty4:3.9.0.Final")
    implementation("org.jboss.resteasy:resteasy-jackson-provider:3.9.0.Final")
    implementation("org.jboss.resteasy:resteasy-jaxb-provider:3.9.0.Final")
    implementation("javax.servlet:javax.servlet-api:4.0.1")

    // dubbo-grpc
    implementation("com.caucho:hessian:4.0.63")
    implementation("com.googlecode.xmemcached:xmemcached:2.4.6")
    implementation("org.apache.cxf:cxf-core:3.4.0")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}

protobuf {

    protoc {
        artifact = "com.google.protobuf:protoc:3.7.0"
    }

    plugins {
        id("grpc-java") {
            artifact = "org.apache.dubbo:protoc-gen-dubbo-java:1.19.0-20191122.130716-5"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc-java")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
