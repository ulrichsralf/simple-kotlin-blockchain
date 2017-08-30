package io.mc.blockchain.node.server.persistence

import org.springframework.data.repository.CrudRepository

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
interface TransactionRepository : CrudRepository<Transaction, String>