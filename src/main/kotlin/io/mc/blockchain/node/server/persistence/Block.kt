package io.mc.blockchain.node.server.persistence

import com.google.common.primitives.Longs
import io.mc.blockchain.node.server.utils.bytesFromHex
import io.mc.blockchain.node.server.utils.toHexString
import org.apache.commons.codec.digest.DigestUtils
import java.util.*

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
data class Block(var version: Long? = null,
                 var index: Long? = null,
                 var previousBlockHash: String? = null,
                 var transactions: List<Transaction>? = null,
                 var nonce: Long? = null,
                 var timestamp: Long? = null,
                 var merkleRoot: String? = null,
                 var hash: String? = null) {

    override fun equals(o: Any?) = this === o || o is Block && hash == o.hash
    override fun hashCode() = hash!!.hashCode()


    companion object {
        fun newBlock(previousBlockHash: String, index: Long, transactions: List<Transaction>, nonce: Long, timestamp: Long = System.currentTimeMillis()): Block {
            val mRoot = transactions.calculateMerkleRoot()
            return Block(
                    version = 1L,
                    index = index,
                    previousBlockHash = previousBlockHash,
                    transactions = transactions,
                    nonce = nonce,
                    timestamp = timestamp,
                    merkleRoot = mRoot,
                    hash = calculateHash(previousBlockHash.bytesFromHex(), mRoot.bytesFromHex(), nonce, timestamp))
        }
    }

}

fun List<Transaction>.calculateMerkleRoot(): String {
    val hashQueue = LinkedList<ByteArray>(this.map { it.senderSignature?.bytesFromHex() })
    while (hashQueue.size > 1) {
        // take 2 hashes from queue
        val hashableData = hashQueue.poll() + hashQueue.poll()
        // put new hash at end of queue
        hashQueue.add(DigestUtils.sha256(hashableData))
    }
    return hashQueue.poll().toHexString()
}

fun calculateHash(previousBlockHash: ByteArray, merkleRoot: ByteArray, nonce: Long, timestamp: Long): String {
    var hashableData = previousBlockHash + merkleRoot
    hashableData += Longs.toByteArray(nonce)
    hashableData += Longs.toByteArray(timestamp)
    return DigestUtils.sha256Hex(hashableData)
}

/**
 * Count the number of bytes in the hash, which are zero at the beginning
 */
fun ByteArray.getLeadingZerosCount(): Int {
    return (0 until size).firstOrNull { this[it].toInt() != 0 } ?: this.size
}
