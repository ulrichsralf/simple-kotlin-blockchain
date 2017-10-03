package io.mc.blockchain.node.server.persistence

import com.google.common.primitives.Longs
import io.mc.blockchain.common.IHashedEntity
import io.mc.blockchain.common.ISignable
import io.mc.blockchain.common.Transaction
import org.apache.commons.codec.digest.DigestUtils
import java.util.*

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
data class Block(override val hash: ByteArray,
                 override val hashData: BlockData) : IHashedEntity {

    override fun equals(other: Any?) = this === other
            || other is Block && Arrays.equals(hash, other.hash)

    override fun hashCode() = hash.hashCode()

    companion object {
        fun newBlock(previousBlockHash: ByteArray,
                     index: Long,
                     transactions: List<Transaction>,
                     nonce: Long,
                     timestamp: Long = System.currentTimeMillis()
        ): Block {
            val blockData = BlockData(
                    1L,
                    index,
                    previousBlockHash,
                    transactions,
                    nonce,
                    timestamp)
            return Block(blockData.sha256Hash(), blockData)
        }
    }
}

fun ISignable.sha256Hash(): ByteArray {
    return DigestUtils.sha256(getSignedBytes())
}


data class BlockData(val version: Long,
                     val index: Long,
                     val previousBlockHash: ByteArray,
                     val transactions: List<Transaction>,
                     val nonce: Long,
                     val timestamp: Long) : ISignable {

    override fun getSignedBytes(): ByteArray {
        return previousBlockHash +
                transactions.calculateMerkleRoot() +
                Longs.toByteArray(nonce) +
                Longs.toByteArray(timestamp)
    }
}

fun List<IHashedEntity>.calculateMerkleRoot(): ByteArray {
    val hashQueue = LinkedList<ByteArray>(this.map { it.hash })
    while (hashQueue.size > 1) {
        val hashableData = hashQueue.poll() + hashQueue.poll()
        hashQueue.add(DigestUtils.sha256(hashableData))
    }
    return if (hashQueue.size == 0) byteArrayOf() else hashQueue.poll()
}


/**
 * Count the number of bytes in the hash, which are zero at the beginning
 */
fun ByteArray.getLeadingZerosCount(): Int {
    return (0 until size).firstOrNull { this[it].toInt() != 0 } ?: this.size
}
