package io.mc.blockchain.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.primitives.Longs
import io.mc.blockchain.node.server.persistence.calculateMerkleRoot
import io.mc.blockchain.node.server.persistence.sha256Hash
import io.mc.blockchain.node.server.utils.SignatureUtils
import java.security.PrivateKey
import java.util.*
import kotlin.reflect.KClass


data class Transaction(
        override var hash: ByteArray? = null,
        override var signature: ByteArray? = null,
        override var hashData: TransactionData? = null) : ISignedEntity, IHashedEntity {

    override fun equals(other: Any?) = this === other || other is Transaction && Arrays.equals(hash, other.hash)
    override fun hashCode() = hash!!.hashCode()
}

data class TransactionData(var text: String? = null,
                           var senderId: ByteArray? = null,
                           var inputs: List<TxInput>? = null,
                           var outputs: List<TxOutput>? = null,
                           var timestamp: Long? = System.currentTimeMillis()) : ISignable {
    override fun getSignedBytes(): ByteArray {
        return text!!.toByteArray() +
                senderId!! +
                inputs!!.sortedBy { it.hashData!!.index!! }.calculateMerkleRoot() +
                outputs!!.sortedBy { it.hashData!!.index!! }.calculateMerkleRoot() +
                Longs.toByteArray(timestamp!!)
    }
}


data class TxInput(
        override var hash: ByteArray? = null,
        override var signature: ByteArray? = null,
        override var hashData: TxInputData? = null
) : IHashedEntity, ISignedEntity, TxData()

data class TxInputData(
        var value: Long? = null,
        var type: String? = null,
        var txHash: ByteArray? = null,
        var index: Int? = null
) : ISignable {
    override fun getSignedBytes() =
            Longs.toByteArray(
                    value!!) +
                    type!!.toByteArray() +
                    txHash!! +
                    index!!.toByte()

}


data class TxOutput(
        override var hash: ByteArray? = null,
        override var hashData: TxOutputData? = null) : IHashedEntity, TxData()

data class TxOutputData(
        var value: Long? = null,
        var type: String? = null,
        var receiverId: ByteArray? = null,
        var index: Int? = null

) : ISignable {
    override fun getSignedBytes() =
            Longs.toByteArray(value!!) +
                    type!!.toByteArray() +
                    receiverId!! +
                    index!!.toByte()
}

fun TxOutputData.toOutput() = TxOutput(sha256Hash(), this)


fun TxInputData.toInput(privateKey: PrivateKey) =
        TxInput(sha256Hash(),
                SignatureUtils.sign(sha256Hash(), privateKey.encoded),
                this)

sealed class TxData

fun Any.toJsonString(): String {
    return ObjectMapper().writeValueAsString(this)
}

fun <T : Any> String.parseJson(type: KClass<T>): T {
    return ObjectMapper().readValue(this, type.java)
}