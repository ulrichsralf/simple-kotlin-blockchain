package io.mc.blockchain.node.server.persistence

import io.mc.blockchain.common.Transaction
import io.mc.blockchain.node.server.utils.toByteString
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class TransactionRepository {

    val pendingMap = mutableMapOf<String, Transaction>()
    val validMap = mutableMapOf<String, Transaction>()

    fun getAllPending(): List<Transaction> {
        return pendingMap.values.toList()
    }

    fun getAllValid(): List<Transaction>{
        return validMap.values.toList()
    }

    fun addNewTransaction(transaction: Transaction): Transaction? {
        pendingMap.put(transaction.hash.toByteString(), transaction)
        return transaction
    }

    fun moveToValid(transaction: Transaction) {
        pendingMap.remove(transaction.hash.toByteString())
        validMap.put(transaction.hash.toByteString(),transaction)
    }

    fun isPending(id: ByteArray): Boolean {
        return pendingMap.containsKey(id.toByteString())
    }


}