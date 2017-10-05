package io.mc.blockchain.node.server.rest


import io.mc.blockchain.common.Transaction
import io.mc.blockchain.node.server.exceptions.ResourceNotFound
import io.mc.blockchain.node.server.service.TransactionService
import io.mc.blockchain.node.server.utils.fromByteString
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("transaction")
class TransactionController @Autowired
constructor(val transactionService: TransactionService) {

    val LOG = getLogger()

    @RequestMapping("/pending")
    fun pendingTx(@RequestParam("senderId", required = false) senderId: String? = null) =
            transactionService.getPendingTransactions(senderId)


    @RequestMapping()
    fun validTx(@RequestParam("senderId", required = false) senderId: String? = null) =
            transactionService.getValidTransactions(senderId)


    @RequestMapping("/{id}")
    fun validTxById(@PathVariable("id") id: String): Transaction? {
        return try {
            transactionService.getValidTransactions().first { Arrays.equals(it.hash, id.fromByteString()) }
        } catch (e: IllegalArgumentException) {
            throw ResourceNotFound()
        } catch (e: NoSuchElementException) {
            throw ResourceNotFound()
        }
    }


    /**
     * Add a new Transaction to the pool.
     * It is expected that the transaction has a valid signature and the correct hash.
     */
    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    internal fun addTransaction(@RequestBody transaction: Transaction, response: HttpServletResponse) {
        LOG.info("Add transaction " + transaction.hash)
        val success = transactionService.add(transaction)
        if (success) {
            response.status = HttpServletResponse.SC_ACCEPTED
        } else {
            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
        }
    }


}
