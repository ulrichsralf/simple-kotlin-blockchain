package io.mc.blockchain.node.server.persistence

import io.mc.blockchain.common.Transaction
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class TransactionRepository {

    val map = mutableMapOf<ByteArray, Transaction>()

    fun findAll(): List<Transaction> {
        return map.values.toList()
    }

    fun save(transaction: Transaction): Transaction? {
        map.put(transaction.hash!!, transaction)
        return transaction
    }

    fun delete(transaction: Transaction) {
        map.remove(transaction.hash)
    }

    fun exists(id: ByteArray): Boolean {
        return map.containsKey(id)
    }


}