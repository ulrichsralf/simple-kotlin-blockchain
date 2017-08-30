package io.mc.blockchain.node.server.persistence

import com.google.common.primitives.Longs
import io.mc.blockchain.node.server.utils.bytesFromHex
import io.mc.blockchain.node.server.utils.toHexString
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.cassandra.core.PrimaryKeyType
import org.springframework.data.annotation.Transient
import org.springframework.data.cassandra.mapping.PrimaryKey
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.mapping.Table
import java.util.*

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Table(value = "blockchain")
data class Block(var version: Long? = 1L,
                 var previousBlockHash: String? = null,
                 var transactions: List<String>? = null,
                 var nonce: Long? = null,
                 var timestamp: Long? = System.currentTimeMillis(),
                 var merkleRoot: String? = transactions?.calculateMerkleRoot(),
                 @PrimaryKeyColumn(name = "partition", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
                 var partition: Long? = null,
                 @PrimaryKeyColumn(name = "hash", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
                 var hash: String? = calculateHash(previousBlockHash!!.bytesFromHex(), merkleRoot!!.bytesFromHex(), nonce!!, timestamp!!)) {

    override fun equals(o: Any?) = this === o || o is Block && hash == o.hash
    override fun hashCode() = hash!!.hashCode()
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