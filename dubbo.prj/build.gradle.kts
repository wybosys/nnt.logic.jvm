import com.google.protobuf.gradle.*
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
    id("com.google.protobuf") version "0.8.16"
    id("org.springframework.boot") version "2.3.4.RELEASE"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "com.nnt"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.16")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.3.4.RELEASE")
    }
}

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/apache-snapshots")
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

springBoot {
    mainClassName = "com.nnt.Dubbo"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

sourceSets {
    main {
        proto {
            srcDir("../src/main/proto")
        }

        kotlin {
            java {
                srcDir("../src/main")

                exclude("com/nnt/flink")
                exclude("com/test/flink")
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

fun <T : Any> OsUse(win: () -> T, unix: () -> T, mac: () -> T): T {
    if (Os.isFamily(Os.FAMILY_WINDOWS))
        return win()
    if (Os.isFamily(Os.FAMILY_MAC))
        return mac()
    return unix()
}

dependencies {

    // kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9-native-mt")

    // logic
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.0")
    implementation("com.ctrip.framework.apollo:apollo-client:1.7.0")
    implementation("it.sauronsoftware.cron4j:cron4j:2.2.5")
    implementation("joda-time:joda-time:2.10.8")
    implementation(
        "com.eclipsesource.j2v8:j2v8_${
            OsUse(
                { "win32_x86_64" },
                { "linux_x86_64" },
                { "macosx_x86_64" })
        }:4.6.0"
    )
    implementation("org.reflections:reflections:0.9.11")
    implementation("org.luaj:luaj-jse:3.0.1")

    // http
    implementation("io.vertx:vertx-core:3.9.4")
    implementation("io.vertx:vertx-web:3.9.4")

    // db
    implementation("org.mybatis:mybatis:3.5.5")
    implementation("com.alibaba:druid:1.1.24")
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("mysql:mysql-connector-java:8.0.21")
    implementation("redis.clients:jedis:3.3.0")
    implementation("org.neo4j.driver:neo4j-java-driver:4.1.1")
    implementation("org.apache.hbase:hbase-client:2.2.6")
    implementation("org.apache.phoenix:phoenix-queryserver-client:5.0.0-HBase-2.0")

    // mq
    implementation("org.apache.kafka:kafka-clients:2.6.0")

    // grpc
    implementation("com.google.protobuf:protobuf-java:3.12.0")
    implementation("com.google.protobuf:protobuf-java-util:3.12.0")
    implementation("io.grpc:grpc-all:1.34.1")
    implementation(files("libs/grpcweb-java-0.1-SNAPSHOT.jar")) // github.com/wybosys/grpcweb-java

    // dubbo
    implementation("org.apache.dubbo:dubbo:2.7.11")
    implementation("org.apache.dubbo:dubbo-dependencies-zookeeper:2.7.11")
    implementation("org.apache.zookeeper:zookeeper:3.4.13") { isForce = true }

    // dubbo-spring
    implementation("org.springframework:spring-remoting:2.0.8")
    implementation("org.springframework:spring-jdbc:4.3.8.RELEASE")

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

    // data
    implementation("joinery:joinery-dataframe:1.9")
    implementation("org.apache.poi:poi:4.1.2")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}

protobuf {

    protoc {
        artifact = "com.google.protobuf:protoc:3.12.0"
    }

    plugins {
        id("grpc-java") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.34.1"
        }

        id("dubbo-grpc") {
            artifact = "io.github.wybosys:dubbo-compiler:0.0.1"
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc-java") {}
                id("dubbo-grpc") {}
            }
        }
    }
}
