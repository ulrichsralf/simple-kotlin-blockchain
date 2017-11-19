package io.mc.blockchain.node.server.service


import io.mc.blockchain.common.Transaction
import io.mc.blockchain.node.server.persistence.AddressRepository
import io.mc.blockchain.node.server.persistence.TransactionRepository
import io.mc.blockchain.node.server.utils.SignatureUtils
import io.mc.blockchain.node.server.utils.fromByteString
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class TransactionService @Autowired constructor(val addressRepository: AddressRepository,
                                                val transactionRepository: TransactionRepository) {

    val LOG = getLogger()


    fun getTransactionPool(): Set<Transaction> {
        return transactionRepository.getAllPending().toSet()
    }

    fun getPendingTransactions(senderId: String? = null): List<Transaction> {
        return transactionRepository.getAllPending().filter { containsAddress(senderId, it) }
    }

    private fun containsAddress(senderId: String?, tx: Transaction): Boolean {
        val byteSenderId = senderId?.fromByteString()
        return senderId == null ||
                Arrays.equals(tx.hashData.senderId, byteSenderId) ||
                tx.hashData.outputs.any {
                    Arrays.equals(it.hashData.receiverId, byteSenderId)
                }
    }

    fun getValidTransactions(senderId: String? = null): List<Transaction> {
        return transactionRepository.getAllValid().filter { containsAddress(senderId, it) }
    }


    /**
     * Add a new Transaction to the pool
     */
    fun add(transaction: Transaction): Boolean {
        return verify(transaction) && transactionRepository.addNewTransaction(transaction) != null
    }

    /**
     * Remove Transaction from pool
     * @param transaction Transaction to moveToValid
     */
    fun moveToValid(transaction: Transaction) {
        transactionRepository.moveToValid(transaction)
    }

    /**
     * Does the pool contain all given Transactions?
     * @param transactions Collection of Transactions to check
     * @return true if all Transactions are member of the pool
     */
    fun containsAll(transactions: Collection<Transaction>): Boolean {
        return transactions.all { transactionRepository.isPending(it.hash) }
    }

    private fun verify(transaction: Transaction): Boolean {
        // correct signature
        val sender = addressRepository.findOne(transaction.hashData.senderId)
        if (sender == null) {
            LOG.warn("Unknown address " + transaction.hashData.senderId)
            return false
        }

        try {
            if (!SignatureUtils.verify(transaction, sender.publicKey)) {
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
