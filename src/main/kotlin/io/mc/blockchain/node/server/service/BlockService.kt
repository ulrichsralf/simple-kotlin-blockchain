package io.mc.blockchain.node.server.service


import io.mc.blockchain.node.server.persistence.*
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*


@Service
class BlockService @Autowired constructor(val transactionService: TransactionService,
                                          val blockRepository: BlockRepository) {

    val LOG = getLogger()


    fun getBlockchain(): List<Block> {
        return blockRepository.findAll().toList()
    }

    /**
     * Determine the last added Block
     *
     * @return Last Block in chain
     */
    val lastBlock: Block?
        get() = if (blockchain.isEmpty()) {
            null
        } else blockchain[blockchain.size - 1]

    /**
     * Append a new Block at the end of chain
     *
     * @param block Block to append
     * @return true if verifcation succeeds and Block was appended
     */
    @Synchronized
    fun append(block: Block): Boolean {
        if (verify(block)) {
            blockchain.add(block)

            // remove transactions from pool
            block.transactions!!.forEach({ transactionService.remove(it) })
            return true
        }
        return false
    }

    /**
     * Download Blocks from other Node and them to the blockchain
     *
     * @param node         Node to query
     * @param restTemplate RestTemplate to use
     */
    fun retrieveBlockchain(node: Node, restTemplate: RestTemplate) {
        val blocks = restTemplate.getForObject(node.address.toString() + "/block", Array<Block>::class.java)
        Collections.addAll(blockchain, *blocks)
        LOG.info("Retrieved " + blocks.size + " blocks from node " + node.address)
    }


    private fun verify(block: Block): Boolean {
        // references last block in chain
        if (blockchain.size > 0) {
            val lastBlockInChainHash = lastBlock!!.hash
            if (!Arrays.equals(block.previousBlockHash, lastBlockInChainHash)) {
                return false
            }
        } else {
            if (block.previousBlockHash != null) {
                return false
            }
        }

        // correct hashes
        if (!Arrays.equals(block.merkleRoot, block.transactions!!.calculateMerkleRoot())) {
            return false
        }
        if (!Arrays.equals(block.hash, calculateHash(block.previousBlockHash!!, block.merkleRoot!!, block.tries!!, block.timestamp!!))) {
            return false
        }

        // transaction limit
        if (block.transactions!!.size > Config.MAX_TRANSACTIONS_PER_BLOCK) {
            return false
        }

        // all transactions in pool
        // considered difficulty
        return transactionService.containsAll(block.transactions!!) && block.hash!!.getLeadingZerosCount() >= Config.DIFFICULTY

    }


}
