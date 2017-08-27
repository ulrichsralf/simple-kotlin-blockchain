package io.mc.blockchain.node.server.rest


import io.mc.blockchain.node.server.persistence.Transaction
import io.mc.blockchain.node.server.service.TransactionService
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("transaction")
class TransactionController @Autowired
constructor(val transactionService: TransactionService) {

    val LOG = getLogger()
    /**
     * Retrieve all Transactions, which aren't in a block yet
     * @return JSON list of Transactions
     */
    internal val transactionPool: Set<Transaction>
        @RequestMapping
        get() = transactionService.getTransactionPool()


    /**
     * Add a new Transaction to the pool.
     * It is expected that the transaction has a valid signature and the correct hash.
     */
    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    internal fun addTransaction(@RequestBody transaction: Transaction, response: HttpServletResponse) {
        LOG.info("Add transaction " + transaction.id)
        val success = transactionService.add(transaction)
        if (success) {
            response.status = HttpServletResponse.SC_ACCEPTED
        } else {
            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
        }
    }


}
