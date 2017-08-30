package io.mc.blockchain.node.server.service


import io.mc.blockchain.node.server.persistence.*
import io.mc.blockchain.node.server.utils.bytesFromHex
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class BlockService @Autowired constructor(val transactionService: TransactionService,
                                          val blockRepository: BlockRepository) {

    val LOG = getLogger()


    fun getBlockchain(): List<Block> {
        return blockRepository.findAll().toList().sortedBy { it.partition }
    }

    /**
     * Determine the last added Block
     *
     * @return Last Block in chain
     */
    fun lastBlock(): Block? {
        return getBlockchain().lastOrNull()
    }

    /**
     * Append a new Block at the end of chain
     *
     * @param block Block to append
     * @return true if verifcation succeeds and Block was appended
     */
    @Synchronized
    fun append(block: Block): Boolean {
        if (verify(block)) {
            blockRepository.save(block)
            // remove transactions from pool
            block.transactions!!.forEach({ transactionService.remove(Transaction.fromJsonString(it)) })
            return true
        }
        return false
    }


    private fun verify(block: Block): Boolean {
        // references last block in chain
        val lastBlockInChainHash = lastBlock()?.hash
        if (block.previousBlockHash != lastBlockInChainHash) {
            return false
        }

        // correct hashes
        if (block.merkleRoot == block.transactions!!.calculateMerkleRoot()) {
            return false
        }
        if (block.hash !=calculateHash(block.previousBlockHash!!.bytesFromHex(), block.merkleRoot!!.bytesFromHex(), block.nonce!!, block.timestamp!!)) {
            return false
        }

        // transaction limit
        if (block.transactions!!.size > Config.MAX_TRANSACTIONS_PER_BLOCK) {
            return false
        }

        // all transactions in pool
        // considered difficulty
        return transactionService.containsAll(block.transactions!!.map { Transaction.fromJsonString(it) }) && block.hash!!.bytesFromHex().getLeadingZerosCount() >= Config.DIFFICULTY

    }


}
