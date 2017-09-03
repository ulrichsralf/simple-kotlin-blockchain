package io.mc.blockchain.node.server.persistence

import com.google.common.primitives.Longs
import io.mc.blockchain.node.server.utils.bytesFromHex
import io.mc.blockchain.node.server.utils.toHexString
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.data.cassandra.mapping.PrimaryKey
import org.springframework.data.cassandra.mapping.Table
import java.util.*

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Table(value = "blockchain")
data class Block(var version: Long? = null,
                 var index: Long? = null,
                 var previousBlockHash: String? = null,
                 var transactions: List<String>? = null,
                 var nonce: Long? = null,
                 var timestamp: Long? = null,
                 var merkleRoot: String? = null,
                 @PrimaryKey
                 var hash: String? = null ) {

    override fun equals(o: Any?) = this === o || o is Block && hash == o.hash
    override fun hashCode() = hash!!.hashCode()


    companion object {
        fun newBlock(previousBlockHash: String, index: Long, transactions: List<String>,nonce: Long,timestamp: Long = System.currentTimeMillis()): Block{
            val mRoot = transactions.calculateMerkleRoot()
            return Block(
                    version = 1L,
                    index = index,
                    previousBlockHash = previousBlockHash,
                    transactions = transactions,
                    nonce = nonce,
                    timestamp = timestamp,
                    merkleRoot = mRoot,
                    hash = calculateHash(previousBlockHash.bytesFromHex(), mRoot.bytesFromHex(), nonce, timestamp) )
        }
    }

}





fun List<String>.calculateMerkleRoot(): String {
    val hashQueue = LinkedList<ByteArray>(this.map { Transaction.fromJsonString(it).signature?.bytesFromHex() })
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
    for (i in 0 until size) {
        if (this[i].toInt() != 0) {
            return i
        }
    }
    return this.size
}