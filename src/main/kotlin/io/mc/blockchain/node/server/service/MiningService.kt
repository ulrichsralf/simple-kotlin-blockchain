package io.mc.blockchain.node.server.service


import io.mc.blockchain.node.server.persistence.Block
import io.mc.blockchain.node.server.persistence.getLeadingZerosCount
import io.mc.blockchain.node.server.utils.bytesFromHex
import io.mc.blockchain.node.server.utils.getLogger
import io.mc.blockchain.node.server.utils.toHexString
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
                    LOG.info("Mined block with " + block.transactions!!.size + " transactions and nonce " + block.nonce)
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
        val previousBlockHash: String? = lastBlock?.hash ?: "start".toByteArray().toHexString()
        val index = (lastBlock?.index ?: 0) + 1
        val transactions = transactionService.getTransactionPool().take(Config.MAX_TRANSACTIONS_PER_BLOCK)


        // sleep if no more transactions left
        if (transactions.isEmpty()) {
            LOG.info("No transactions available, pausing")
            Thread.sleep(10000)
            return null
        }
        LOG.info("Start mining new block")
        // try new block until difficulty is sufficient
        while (true)
            try {
                Thread.sleep(0)
                val block = Block.newBlock(previousBlockHash = previousBlockHash!!, index = index, transactions = transactions.map { it.toJsonString() }, nonce = tries)
                if (block.hash!!.bytesFromHex().getLeadingZerosCount() >= Config.DIFFICULTY) return block
                tries++
            } catch (e: Exception) {
                LOG.error(e.message)
            }
    }

}
