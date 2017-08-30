package io.mc.blockchain.node.server.service


import io.mc.blockchain.node.server.persistence.AddressRepository
import io.mc.blockchain.node.server.persistence.Transaction
import io.mc.blockchain.node.server.persistence.TransactionRepository
import io.mc.blockchain.node.server.utils.SignatureUtils
import io.mc.blockchain.node.server.utils.bytesFromHex
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class TransactionService @Autowired constructor(val addressRepository: AddressRepository,
                                                val transactionRepository: TransactionRepository) {

    val LOG = getLogger()


    fun getTransactionPool(): Set<Transaction> {
        return transactionRepository.findAll().toSet()
    }

    /**
     * Add a new Transaction to the pool
     */
    fun add(transaction: Transaction): Boolean {
        return verify(transaction) && transactionRepository.save(transaction) != null
    }

    /**
     * Remove Transaction from pool
     * @param transaction Transaction to remove
     */
    fun remove(transaction: Transaction) {
        transactionRepository.delete(transaction)
    }

    /**
     * Does the pool contain all given Transactions?
     * @param transactions Collection of Transactions to check
     * @return true if all Transactions are member of the pool
     */
    fun containsAll(transactions: Collection<Transaction>): Boolean {
        return transactions.all { transactionRepository.exists(it.id) }
    }

    private fun verify(transaction: Transaction): Boolean {
        // correct signature
        val sender = addressRepository.findOne(transaction.senderId)
        if (sender == null) {
            LOG.warn("Unknown address " + transaction.senderId)
            return false
        }

        try {
            if (!SignatureUtils.verify(transaction, sender.publicKey!!.bytesFromHex())) {
                LOG.warn("Invalid signature")
                return false
            }
        } catch (e: Exception) {
            LOG.error("Error while verification", e)
            return false
        }
        return true
    }


}
