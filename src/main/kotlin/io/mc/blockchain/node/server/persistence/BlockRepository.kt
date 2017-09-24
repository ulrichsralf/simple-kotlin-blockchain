package io.mc.blockchain.node.server.persistence

import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class BlockRepository {


    val map: HTreeMap<String, Any>

    init {
        map = DBMaker.memoryDB().make().hashMap("block", Serializer.STRING, Serializer.JAVA).create()
    }

    fun findAll(): List<Block> {
       return map.values.filterIsInstance(Block::class.java)
    }

    fun save(block: Block) {
       map.put(block.hash,block)
    }


}