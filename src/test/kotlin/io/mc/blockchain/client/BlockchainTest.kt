package io.mc.blockchain.client

import io.mc.blockchain.node.server.utils.toByteString
import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * @author Ralf Ulrich
 * 31.08.17
 */
class BlockchainTest {

    val currency = "VPF"

    val client = BlockchainClient("http://localhost:8080")

    @Test
    fun testAddAddress() {
        val pub = client.generateKeyPair().public
        val address = client.generateAddress(pub)
        client.publishAddress(address)
        val returnedAddress = client.getAddress(address.id!!)
        println(returnedAddress)
    }

    @Test
    fun testTransfer() {

        val keyPair = client.generateKeyPair()
        val address1 = client.generateAddress(keyPair.public)

        client.publishAddress(address1)
        client.initTx(keyPair.private, currency, 100, "Hello Blockchain", address1)
        client.await()
        assertEquals(100L, client.getBalance(address1)[currency])

        val other = client.generateKeyPair()
        val otherAddress = client.generateAddress(other.public)
        client.transfer(keyPair.private, currency, 50, address1, otherAddress, "Here you go!")
        client.await()

        assertEquals(50L, client.getBalance(address1)[currency])
        assertEquals(50L, client.getBalance(otherAddress)[currency])

    }

    @Test
    fun testTransferMulti() {

        val keyPair = client.generateKeyPair()
        val address1 = client.generateAddress(keyPair.public)

        client.publishAddress(address1)
        println(address1.id!!.toByteString())
        client.initTx(keyPair.private, currency, 100, "Hello Blockchain", address1)
        client.await()
        assertEquals(100L, client.getBalance(address1)[currency])
        val other = client.generateKeyPair()
        val otherAddress = client.generateAddress(other.public)

        for (i in 1..100) {
            client.transfer(keyPair.private, currency, 1, address1, otherAddress, "Here you go!")
            client.await()
            println(client.getBalance(address1))
            println(client.getBalance(otherAddress))
        }

        client.await()
        assertEquals(0L, client.getBalance(address1)[currency])
        assertEquals(100L, client.getBalance(otherAddress)[currency])
    }
}

fun BlockchainClient.await() {
    while (getPendingTransactions().size != 0) {
        Thread.sleep(100)
    }
}