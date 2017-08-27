package io.mc.blockchain.node.server.service


object Config {

    /**
     * Address of a Node to use for initialization
     */
    val MASTER_NODE_ADDRESS = "http://localhost:8080"

    /**
     * Minimum number of leading zeros every block hash has to fulfill
     */
    val DIFFICULTY = 3

    /**
     * Maximum numver of Transactions a Block can hold
     */
    val MAX_TRANSACTIONS_PER_BLOCK = 5


}
