package io.mc.blockchain.node.server.persistence

import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class TransactionRepository {

    val map: HTreeMap<String, Any>

    init {
        map = DBMaker.memoryDB().make().hashMap("transaction", Serializer.STRING, Serializer.JAVA).create()
    }

    fun findAll(): List<Transaction> {
      return map.values.filterIsInstance(Transaction::class.java)
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