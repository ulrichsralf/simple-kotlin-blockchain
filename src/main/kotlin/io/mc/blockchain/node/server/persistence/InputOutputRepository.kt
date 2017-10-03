package io.mc.blockchain.node.server.persistence

import io.mc.blockchain.common.TxInput
import io.mc.blockchain.common.TxOutput
import io.mc.blockchain.node.server.utils.toBase64String
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class InputOutputRepository @Autowired constructor(val txRepo: TransactionRepository) {


    val txInfoMap = mutableMapOf<String, MutableList<TxInfo>>()

    fun findAll(id: String): List<TxInfo> {
        return txInfoMap.get(id).orEmpty()
    }

    fun update(block: Block) {
        block.hashData.transactions.forEach { tx ->
            tx.hashData?.outputs?.forEach {
                txInfoMap.getOrPut(it.hashData!!.receiverId!!.toBase64String(), { mutableListOf() }).add(TxInfo(tx.hash!!, it, null))
            }
            tx.hashData?.inputs?.forEach { inp ->
                val correspondingOut = txRepo.validMap.get(inp.hashData!!.txHash!!.toBase64String())?.hashData!!.outputs!!
                        .filter { it.hashData!!.index == inp.hashData!!.index }
                        .first()
                txInfoMap.getOrPut(correspondingOut.hashData!!.receiverId!!.toBase64String(), { mutableListOf() }).add(TxInfo(tx.hash!!, null, inp))
            }
        }
    }
}

data class TxInfo(val txHash: ByteArray, val txOut: TxOutput?, val txIn: TxInput?)