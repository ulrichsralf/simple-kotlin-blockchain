package io.mc.blockchain.node.server.persistence

import io.mc.blockchain.node.server.utils.toByteString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class BlockRepository @Autowired constructor(val inout: InputOutputRepository) {

    val map = mutableMapOf<String, Block>()

    fun findAll(): List<Block> {
        return map.values.toList()
    }

    fun save(block: Block) {
        map.put(block.hash.toByteString(), block)
        inout.update(block)
    }


}