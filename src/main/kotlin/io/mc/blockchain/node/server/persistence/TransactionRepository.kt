package io.mc.blockchain.node.server.persistence

import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class TransactionRepository {

    val map = mutableMapOf<String, Transaction>()

    fun findAll(): List<Transaction> {
        return map.values.toList()
    }

    fun save(transaction: Transaction): Transaction? {
        return map.put(transaction.id!!, transaction)
    }

    fun delete(transaction: Transaction) {
        map.remove(transaction.id)
    }

    fun exists(id: String?): Boolean {
        return map.containsKey(id)
    }


}