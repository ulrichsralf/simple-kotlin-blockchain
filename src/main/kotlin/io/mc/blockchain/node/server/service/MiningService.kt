package io.mc.blockchain.node.server.service


import io.mc.blockchain.node.server.persistence.Block
import io.mc.blockchain.node.server.persistence.getLeadingZerosCount
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MiningService @Autowired
constructor(private val transactionService: TransactionService, private val blockService: BlockService) {


    val LOG = getLogger()

    init {
        Thread {
            while (true) {
                val block = mineBlock()
                if (block != null) {
                    // Found block! Append and publish
                    LOG.info("Mined block with " + block.hashData.transactions.size + " transactions and nonce " + block.hashData.nonce)
                    blockService.append(block)
                    // TODO nodeService.broadcastPut("block", block)
                }
            }
        }.start()
    }


    private fun mineBlock(): Block? {
        var tries: Long = 0

        // get previous hash and transactions
        val lastBlock = blockService.lastBlock()
        val previousBlockHash: ByteArray = lastBlock?.hash ?: "start".toByteArray()
        val index = (lastBlock?.hashData?.index ?: 0) + 1
        val transactions = transactionService.getTransactionPool().take(Config.MAX_TRANSACTIONS_PER_BLOCK)


        // sleep if no more transactions left
        if (transactions.isEmpty()) {
           // LOG.info("No transactions available, pausing")
            Thread.sleep(100)
            return null
        }
        LOG.info("Start mining new block")
        // try new block until difficulty is sufficient
        while (true)
            try {
                Thread.sleep(0)
                val block = Block.newBlock(previousBlockHash = previousBlockHash, index = index, transactions = transactions, nonce = tries)
                if (block.hash.getLeadingZerosCount() >= Config.DIFFICULTY) return block
                tries++
            } catch (e: Exception) {
                LOG.error(e.message)
            }
    }

}
