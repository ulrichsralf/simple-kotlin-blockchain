package io.mc.blockchain.node.server.persistence

import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class BlockRepository {

    val map = mutableMapOf<ByteArray, Block>()

    fun findAll(): List<Block> {
        return map.values.toList()
    }

    fun save(block: Block) {
        map.put(block.hash, block)
    }


}