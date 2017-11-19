package io.mc.blockchain.common

import com.google.common.primitives.Longs
import io.mc.blockchain.node.server.persistence.calculateMerkleRoot
import io.mc.blockchain.node.server.persistence.sha256Hash
import io.mc.blockchain.node.server.utils.SignatureUtils
import java.security.PrivateKey
import java.util.*


data class Transaction(
        override val hash: ByteArray,
        override val signature: ByteArray,
        override val hashData: TransactionData) : ISignedEntity, IHashedEntity {

    override fun equals(other: Any?) = this === other || other is Transaction && Arrays.equals(hash, other.hash)
    override fun hashCode() = hash.hashCode()
}

data class TransactionData(val text: String,
                           val senderId: ByteArray,
                           val inputs: List<TxInput>,
                           val outputs: List<TxOutput>,
                           val timestamp: Long = System.currentTimeMillis()) : ISignable {
    override fun getSignedBytes(): ByteArray {
        return text.toByteArray() +
                senderId +
                inputs.sortedBy { it.hashData.index }.calculateMerkleRoot() +
                outputs.sortedBy { it.hashData.index }.calculateMerkleRoot() +
                Longs.toByteArray(timestamp)
    }
}


data class TxInput(
        override val hash: ByteArray,
        override val signature: ByteArray,
        override val hashData: TxInputData
) : IHashedEntity, ISignedEntity, TxData()

data class TxInputData(
        val value: Long,
        val type: String,
        val txHash: ByteArray,
        val index: Int
) : ISignable {
    override fun getSignedBytes() =
            Longs.toByteArray(
                    value) +
                    type.toByteArray() +
                    txHash +
                    index.toByte()
}


data class TxOutput(
        override val hash: ByteArray,
        override val hashData: TxOutputData) : IHashedEntity, TxData()

data class TxOutputData(
        val value: Long,
        val type: String,
        val receiverId: ByteArray,
        val index: Int,
        val txHash: ByteArray

) : ISignable {
    override fun getSignedBytes() =
            Longs.toByteArray(value) +
                    type.toByteArray() +
                    receiverId +
                    index.toByte()
}

fun TxOutputData.toOutput() = TxOutput(sha256Hash(), this)


fun TxInputData.toInput(privateKey: PrivateKey) =
        TxInput(sha256Hash(),
                SignatureUtils.sign(sha256Hash(), privateKey.encoded),
                this)

sealed class TxData
