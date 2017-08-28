package io.mc.blockchain.node.server.persistence

import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
interface BlockRepository : CrudRepository<Block, String>