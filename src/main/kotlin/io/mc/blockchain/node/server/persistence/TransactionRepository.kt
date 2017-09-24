package io.mc.blockchain.node.server.persistence

import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class TransactionRepository {

    val map: HTreeMap<String, Transaction>

    init {
        map = DBMaker.memoryDB().make().hashMap("transaction").create() as HTreeMap<String, Transaction>
    }

    fun findAll(): List<Transaction> {
        return map.values.toList().filterNotNull()
    }

    fun save(transaction: Transaction): Transaction? {
        return map.put(transaction.id, transaction) as Transaction?
    }

    fun delete(transaction: Transaction) {
        map.remove(transaction.id)
    }

    fun exists(id: String?): Boolean {
        return map.containsKey(id)
    }


}