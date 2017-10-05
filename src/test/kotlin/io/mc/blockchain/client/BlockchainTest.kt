package io.mc.blockchain.client

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
            client.publishAddress(address)
            client.initTx(keyPair.private, "VPF",100,"Hello Blockchain", address)
            //client.publishTransaction(transaction)
           // println(client.getPendingTransactions())
            Thread.sleep(10000)

            val other = client.generateKeyPair()
            val otherAddress = client.generateAddress(other.public)
            client.transfer(keyPair.private,"VPF",50,address,otherAddress,"Here you go!")
            println(client.getTransactions(address))
            println(client.getPendingTransactions(address))

    }

}