package io.mc.blockchain.node.server.persistence


import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.primitives.Longs
import io.mc.blockchain.node.server.utils.bytesFromHex
import io.mc.blockchain.node.server.utils.toHexString
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.data.cassandra.mapping.PrimaryKey
import org.springframework.data.cassandra.mapping.Table

@Table("transaction")
data class Transaction(
        /**
         * Unique identifier which can be generated by hashing text, senderHash, signature and timestamp
         */
        @PrimaryKey
        var id: String? = null,
        /**
         * Payload of this transaction
         */
        var text: String? = null,
        /**
         * The hash of the address which is responsible for this Transaction
         */
        var senderId: String? = null,
        /**
         * Creation time of this Transaction
         */
        var timestamp: Long? = System.currentTimeMillis(),

        /**
         * Signature of text which can be verified with publicKey of sender address
         */
        var signature: String? = null) {


    override fun equals(o: Any?) = this === o || o is Transaction && id == o.id
    override fun hashCode() = id!!.hashCode()


    fun getSignData(): ByteArray {
        return  text!!.bytesFromHex()
    }

    fun toJsonString(): String {
        return ObjectMapper().writeValueAsString(this)
    }

    companion object {
        fun fromJsonString(transaction: String): Transaction{
            return ObjectMapper().readValue(transaction,Transaction::class.java)
        }

        fun calculateHashId(text: String,senderId: String, signature: String, timestamp: Long): String {
            return DigestUtils.sha256( text.bytesFromHex()+ senderId.bytesFromHex()+ signature.bytesFromHex() +Longs.toByteArray(timestamp)).toHexString()
        }


    }
}
