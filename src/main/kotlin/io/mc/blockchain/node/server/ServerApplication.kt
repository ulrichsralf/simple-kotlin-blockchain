package io.mc.blockchain.node.server

import io.mc.blockchain.node.server.config.CassandraConfig
import io.mc.blockchain.node.server.rest.BlockController
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ServerApplication::class.java, *args)
}
