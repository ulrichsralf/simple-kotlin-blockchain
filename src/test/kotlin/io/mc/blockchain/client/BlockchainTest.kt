package io.mc.blockchain.client

import io.mc.blockchain.node.server.utils.toByteString
import org.junit.Test

/**
 * @author Ralf Ulrich
 * 31.08.17
 */
class BlockchainTest {


    val client = BlockchainClient("http://vpf.mind-score.de")

    @Test
    fun testAddAddress() {
        val pub = client.generateKeyPair().public
        val address = client.generateAddress(pub)
        client.publishAddress(address)
        val returnedAddress = client.getAddress(address.id!!)
        println(returnedAddress)
    }

    @Test
    fun testAddTransaction() {

        val keyPair = client.generateKeyPair()
        val address = client.generateAddress(keyPair.public)
        println(address.id!!.toByteString())
        client.publishAddress(address)
        client.initTx(keyPair.private, "VPF", 100, "Hello Blockchain", address)
        //client.publishTransaction(transaction)
        println(client.getPendingTransactions().count())
        println(client.getTransactions().count())
        Thread.sleep(10000)
        println(client.getBalance(address))

        val other = client.generateKeyPair()
        val otherAddress = client.generateAddress(other.public)
        client.transfer(keyPair.private, "VPF", 50, address, otherAddress, "Here you go!")

        Thread.sleep(10000)


        println(client.getTransactions(address).count())
       // println(client.getPendingTransactions(address))
        println(client.getBalance(address))
        println(client.getBalance(otherAddress))

    }

}