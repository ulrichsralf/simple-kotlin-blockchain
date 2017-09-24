package io.mc.blockchain.node.server.service


import io.mc.blockchain.node.server.persistence.*
import io.mc.blockchain.node.server.utils.bytesFromHex
import io.mc.blockchain.node.server.utils.getLogger
import io.mc.blockchain.node.server.utils.toHexString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class BlockService @Autowired constructor(val transactionService: TransactionService,
                                          val blockRepository: BlockRepository) {

    val LOG = getLogger()


    fun getBlockchain(): List<Block> {
        return blockRepository.findAll().toList().sortedByDescending { it.timestamp }
    }

    /**
     * Determine the last added Block
     *
     * @return Last Block in chain
     */
    fun lastBlock(): Block? {
        return getBlockchain().firstOrNull()
    }

    /**
     * Append a new Block at the end of chain
     *
     * @param block Block to append
     * @return true if verifcation succeeds and Block was appended
     */
    @Synchronized
    fun append(block: Block): Boolean {
        return if (verify(block)) {
            blockRepository.save(block)
            LOG.info("Block valid, adding to chain")
            // remove transactions from pool
            block.transactions!!.forEach({ transactionService.remove(it) })
            true
        } else {
            LOG.warn("Block is invalid! $block")
            false
        }
    }


    private fun verify(block: Block): Boolean {
        // references last block in chain
        val lastBlock = lastBlock()
        val lastBlockInChainHash = lastBlock?.hash ?: "start".toByteArray().toHexString()
        val lastIndex = lastBlock?.index ?: 0

        if (block.previousBlockHash != lastBlockInChainHash) return false

        // correct hashes
        if (block.merkleRoot != block.transactions!!.calculateMerkleRoot()) return false

        // correct index
        if (block.index != lastIndex + 1) return false

        if (block.hash != calculateHash(block.previousBlockHash!!.bytesFromHex(), block.merkleRoot!!.bytesFromHex(), block.nonce!!, block.timestamp!!)) {
            return false
        }

        // transaction limit
        if (block.transactions!!.size > Config.MAX_TRANSACTIONS_PER_BLOCK) return false


        // all transactions in pool
        // considered difficulty
        return transactionService.containsAll(block.transactions!!) && block.hash!!.bytesFromHex().getLeadingZerosCount() >= Config.DIFFICULTY

    }


}
