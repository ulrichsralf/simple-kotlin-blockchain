package io.mc.blockchain.node.server.persistence

import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class BlockRepository {


    val map: HTreeMap<String, Block>

    init {
        map = DBMaker.memoryDB().make().hashMap("block").create() as HTreeMap<String, Block>
    }

    fun findAll(): List<Block> {
        return map.values.toList().filterNotNull()
    }

    fun save(block: Block) {
        map.put(block.hash, block)
    }


}