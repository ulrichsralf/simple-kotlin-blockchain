package io.mc.blockchain.client


import feign.Feign
import feign.Headers
import feign.Param
import feign.RequestLine
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import io.mc.blockchain.common.*
import io.mc.blockchain.node.server.config.JacksonConfig
import io.mc.blockchain.node.server.utils.SignatureUtils
import io.mc.blockchain.node.server.utils.getLogger
import io.mc.blockchain.node.server.utils.toByteString
import org.apache.commons.codec.digest.DigestUtils
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*


class BlockchainClient(serverNode: String = "http://localhost:8080") {


    private val restClient = Feign.builder()
            .encoder(JacksonEncoder(listOf(JacksonConfig.getBase64Module())))
            .decoder(JacksonDecoder(listOf(JacksonConfig.getBase64Module())))
            .target(Blockchain::class.java, serverNode)
    private val LOG = getLogger()


    fun generateKeyPair(): KeyPair {
        return SignatureUtils.generateKeyPair()
    }

    fun generateAddress(key: PublicKey): Address {
        val hash = DigestUtils.sha256(key.encoded)
        return Address(hash, key.encoded)
    }

    fun publishAddress(address: Address) {
        restClient.addAddress(address)
        LOG.info("Added $address")
    }

    fun publishTransaction(transaction: Transaction) {
        restClient.addTransaction(transaction)
        LOG.info("Added $transaction")
    }

    fun transfer(privateKey: PrivateKey,
                 currency: String,
                 value: Long,
                 from: Address,
                 to: Address,
                 comment: String) {

        val txIn = getTransactions()
                .filter {
                    Arrays.equals(it.hashData?.senderId, from.id) &&
                            it.hashData?.outputs.orEmpty()
                                    .any { it.hashData?.type == currency }
                }
        val firstIn = txIn.takeWhile {
            it.hashData?.outputs.orEmpty()
                    .filter { it.hashData?.type == currency }
                    .sumByDouble { (it.hashData?.value ?: 0).toDouble() } >= value
        }

        var totalIn = 0L
        val inputs = firstIn.map { tx ->
            tx.hashData!!.outputs.orEmpty().map {
                totalIn += it.hashData!!.value!!
                TxInputData(it.hashData!!.value, it.hashData!!.type, tx.hash, it.hashData!!.index).toInput(privateKey)
            }

        }.flatten()
        val diff = totalIn - value
        if (diff < 0) throw IllegalStateException("not enough deposit")
        val out = mutableListOf(TxOutputData(value, currency, to.id, 1).toOutput())
        if (diff > 0) {
            out.add(TxOutputData(diff,currency,from.id,2).toOutput())
        }

        val payload = TransactionData(comment, from.id, inputs, out, System.currentTimeMillis())
        val id = DigestUtils.sha256(payload.getSignedBytes())
        val signature = SignatureUtils.sign(id, privateKey.encoded)
        publishTransaction(  Transaction(id, signature, payload))
    }

    fun initTx(privateKey: PrivateKey,currency: String,value: Long,comment: String,to: Address){
        val payload = TransactionData(comment, to.id, listOf(), listOf(TxOutputData(value,currency,to.id,1).toOutput()), System.currentTimeMillis())
        val id = DigestUtils.sha256(payload.getSignedBytes())
        val signature = SignatureUtils.sign(id, privateKey.encoded)
        publishTransaction(  Transaction(id, signature, payload))
    }

    fun getAddress(id: ByteArray): Address {
        return restClient.getAddress(id.toByteString())
                ?: throw IllegalArgumentException("address ${id.toByteString()} not found")
    }

    fun getTransactions(): List<Transaction> {
        return restClient.getTransactions()
    }

    fun getPendingTransactions(): List<Transaction> {
        return restClient.getPendingTransactions()
    }
}


interface Blockchain {

    @RequestLine("PUT /address")
    @Headers("Content-Type: application/json")
    fun addAddress(address: Address)

    @RequestLine("GET /address/{id}")
    @Headers("Content-Type: application/json")
    fun getAddress(@Param("id") id: String): Address?


    @RequestLine("PUT /transaction")
    @Headers("Content-Type: application/json")
    fun addTransaction(transaction: Transaction)

    @RequestLine("GET /transaction")
    @Headers("Content-Type: application/json")
    fun getTransactions(): List<Transaction>

    @RequestLine("GET /transaction/pending")
    @Headers("Content-Type: application/json")
    fun getPendingTransactions(): List<Transaction>


}