package io.mc.blockchain.node.server.service


import io.mc.blockchain.node.server.persistence.Block
import io.mc.blockchain.node.server.persistence.Transaction
import io.mc.blockchain.node.server.persistence.getLeadingZerosCount
import io.mc.blockchain.node.server.utils.bytesFromHex
import io.mc.blockchain.node.server.utils.getLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors

@Service
class MiningService @Autowired
constructor(private val transactionService: TransactionService, private val blockService: BlockService) : Runnable {

    private val runMiner = AtomicBoolean(false)
    val LOG = getLogger()

    /**
     * Start the miner
     */
    fun startMiner() {
        if (runMiner.compareAndSet(false, true)) {
            LOG.info("Starting miner")
            val thread = Thread(this)
            thread.start()
        }
    }

    /**
     * Stop the miner after next iteration
     */
    fun stopMiner() {
        LOG.info("Stopping miner")
        runMiner.set(false)
    }

    /**
     * Loop for new blocks until someone signals to stop
     */
    override fun run() {
        while (runMiner.get()) {
            val block = mineBlock()
            if (block != null) {
                // Found block! Append and publish
                LOG.info("Mined block with " + block.transactions!!.size + " transactions and nonce " + block.nonce)
                blockService.append(block)
                // TODO nodeService.broadcastPut("block", block)
            }
        }
        LOG.info("Miner stopped")
    }

    private fun mineBlock(): Block? {
        var tries: Long = 0

        // get previous hash and transactions
        val previousBlockHash: String? = blockService.lastBlock()?.hash
        val transactions = transactionService.getTransactionPool().take(Config.MAX_TRANSACTIONS_PER_BLOCK)


        // sleep if no more transactions left
        if (transactions.isEmpty()) {
            LOG.info("No transactions available, pausing")
            try {
                Thread.sleep(10000)
            } catch (e: InterruptedException) {
                LOG.error("Thread interrupted", e)
            }
            return null
        }

        // try new block until difficulty is sufficient
        while (runMiner.get()) {
            val block = Block(previousBlockHash = previousBlockHash, transactions = transactions, nonce = tries)
            if (block.hash!!.bytesFromHex().getLeadingZerosCount() >= Config.DIFFICULTY) {
                return block
            }
            tries++
        }
        return null
    }

}
