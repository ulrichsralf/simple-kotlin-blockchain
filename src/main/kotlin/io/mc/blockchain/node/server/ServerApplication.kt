package io.mc.blockchain.node.server

import io.mc.blockchain.node.server.config.CassandraConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackageClasses = arrayOf(CassandraConfig::class))
class ServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ServerApplication::class.java, *args)
}
