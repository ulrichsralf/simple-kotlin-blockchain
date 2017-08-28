package io.mc.blockchain.node.server.rest


import io.mc.blockchain.node.server.persistence.Block
import io.mc.blockchain.node.server.service.BlockService
import io.mc.blockchain.node.server.service.MiningService
import io.mc.blockchain.node.server.utils.encodeBase64String
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("block")
class BlockController @Autowired
constructor(val blockService: BlockService,
            val miningService: MiningService) {

    val LOG = getLogger()
    /**
     * Retrieve all Blocks in order of mine date, also known as Blockchain
     * @return JSON list of Blocks
     */
    @RequestMapping
    fun  blockchain() : List<Block>{
        return blockService.getBlockchain()
    }

    /**
     * Add a new Block at the end of the Blockchain.
     * It is expected that the Block is valid, see BlockService.verify(Block) for details.
     *
     * @param block the Block to add
     * @param publish if true, this Node is going to inform all other Nodes about the new Block
     * @param response Status Code 202 if Block accepted, 406 if verification fails
     */
    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    internal fun addBlock(@RequestBody block: Block, @RequestParam(required = false) publish: Boolean?, response: HttpServletResponse) {
        LOG.info("Add block " + block.hash!!)
        val success = blockService.append(block)

        if (success) {
            response.status = HttpServletResponse.SC_ACCEPTED

            if (publish != null && publish) {
                // TODO ?  nodeService.broadcastPut("block", block)
            }
        } else {
            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
        }
    }

    /**
     * Start mining of Blocks on this Node in a Thread
     */
    @RequestMapping(path = arrayOf("start-miner"))
    fun startMiner() {
        miningService.startMiner()
    }

    /**
     * Stop mining of Blocks on this Node
     */
    @RequestMapping(path = arrayOf("stop-miner"))
    fun stopMiner() {
        miningService.stopMiner()
    }


}
