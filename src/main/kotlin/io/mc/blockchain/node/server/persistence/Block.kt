package io.mc.blockchain.node.server.persistence

import com.google.common.primitives.Longs
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.data.cassandra.mapping.PrimaryKey
import org.springframework.data.cassandra.mapping.Table
import java.util.*

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Table(value = "blockchain")
data class Block(@PrimaryKey("id") var id: UUID? = UUID.randomUUID(),
                 var previousBlockHash: ByteArray? = null,
                 var transactions: List<Transaction>? = null,
                 var tries: Long? = null,
                 var timestamp: Long? = System.currentTimeMillis(),
                 var merkleRoot: ByteArray? = transactions?.calculateMerkleRoot(),
                 var hash: ByteArray? = calculateHash(previousBlockHash!!, merkleRoot!!, tries!!, timestamp!!)) {

    override fun equals(o: Any?) = this === o || o is Block && Arrays.equals(hash, o.hash)
    override fun hashCode() = Arrays.hashCode(hash)
}


/**
 * Calculates the Hash of all transactions as hash tree.
 * https://en.wikipedia.org/wiki/Merkle_tree
 * @return SHA256-hash as raw bytes
 */
fun List<Transaction>.calculateMerkleRoot(): ByteArray {
    val hashQueue = LinkedList<ByteArray>(this.map { it.signature })
    while (hashQueue.size > 1) {
        // take 2 hashes from queue
        val hashableData = hashQueue.poll() + hashQueue.poll()
        // put new hash at end of queue
        hashQueue.add(DigestUtils.sha256(hashableData))
    }
    return hashQueue.poll()
}


/**
 * Calculates the hash using relevant fields of this type
 * @return SHA256-hash as raw bytes
 */
fun calculateHash(previousBlockHash: ByteArray, merkleRoot: ByteArray, tries: Long, timestamp: Long): ByteArray {
    var hashableData = previousBlockHash + merkleRoot
    hashableData += Longs.toByteArray(tries)
    hashableData += Longs.toByteArray(timestamp)
    return DigestUtils.sha256(hashableData)
}


/**
 * Count the number of bytes in the hash, which are zero at the beginning
 * @return int number of leading zeros
 */
fun ByteArray.getLeadingZerosCount(): Int {
    for (i in 0 until size) {
        if (this[i].toInt() != 0) {
            return i
        }
    }
    return this.size
}