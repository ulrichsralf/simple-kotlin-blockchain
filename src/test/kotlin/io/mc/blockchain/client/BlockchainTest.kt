package io.mc.blockchain.client

import junit.framework.TestCase.assertEquals
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
        client.initTx(keyPair.private, currency, 100, "Hello Blockchain", address1)
        client.await()
        assertEquals(100L, client.getBalance(address1)[currency])
        val other = client.generateKeyPair()
        val otherAddress = client.generateAddress(other.public)

        for (i in 1..10) {
            client.transfer(keyPair.private, currency, 10, address1, otherAddress, "Here you go!")
            client.await()
            assertEquals(100L, (client.getBalance(address1)[currency] ?: 0L) +
                    (client.getBalance(otherAddress)[currency] ?: 0L)
            )
        }

        client.await()
        assertEquals(0L, client.getBalance(address1)[currency] ?: 0L)
        assertEquals(100L, client.getBalance(otherAddress)[currency])
    }
}

fun BlockchainClient.await() {
    while (getPendingTransactions().size != 0) {
        Thread.sleep(1000)
    }
}