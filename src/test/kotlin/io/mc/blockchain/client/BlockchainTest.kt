package io.mc.blockchain.client

import org.junit.Test
import java.nio.file.Paths

/**
 * @author Ralf Ulrich
 * 31.08.17
 */
class BlockchainTest {


    val client = BlockchainClient("http://vpf.mind-score.de")

    @Test
    fun testAddAddress() {
        client.generateKeyPair()
        client.publishAddress(client.generateAddress( Paths.get("key.pub")))
    }

    @Test
    fun testAddTransaction(){
        client.generateKeyPair()
        val address = client.generateAddress(Paths.get("key.pub"))
        client.publishAddress(address)
        val transaction = client.generateTransaction(Paths.get("key.priv"), "Hello Blockchain", address.id!!)
        client.publishTransaction(transaction)

    }

}