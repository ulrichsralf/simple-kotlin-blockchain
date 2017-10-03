package io.mc.blockchain.node.server.persistence

import io.mc.blockchain.common.TxData
import io.mc.blockchain.node.server.utils.toByteString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class InputOutputRepository @Autowired constructor(val txRepo: TransactionRepository) {


    val txInfoMap = mutableMapOf<String, MutableList<TxData>>()

    fun findAll(id: String): List<TxData> {
        return txInfoMap.get(id).orEmpty()
    }

    fun update(block: Block) {
        block.hashData.transactions.forEach { tx ->
            tx.hashData?.outputs?.forEach {
                txInfoMap.getOrPut(it.hashData!!.receiverId!!.toByteString(), { mutableListOf() }).add(it)
            }
            tx.hashData?.inputs?.forEach { inp ->
                val correspondingOut = txRepo.validMap.get(inp.hashData!!.txHash!!.toByteString())?.hashData!!.outputs!!
                        .filter { it.hashData!!.index == inp.hashData!!.index }
                        .first()
                txInfoMap.getOrPut(correspondingOut.hashData!!.receiverId!!.toByteString(), { mutableListOf() }).add(inp)
            }
        }
    }
}
