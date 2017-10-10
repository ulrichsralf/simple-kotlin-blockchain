package io.mc.blockchain.node.server.persistence

import io.mc.blockchain.client.index
import io.mc.blockchain.common.TxOutput
import io.mc.blockchain.node.server.utils.toByteString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class InputOutputRepository @Autowired constructor(val txRepo: TransactionRepository) {


    val unspendMap = mutableMapOf<String, MutableList<TxOutput>>()
    val txIndex = mutableMapOf<String, TxOutput>()

    fun findUnspend(id: String): List<TxOutput> {
        return unspendMap.get(id).orEmpty()
    }

    fun update(block: Block) {
        block.hashData.transactions.forEach { tx ->
            tx.hashData?.outputs?.forEach {
                val outWithTx = it.addTxHash(tx.hash)
                println("add " +it.hashData!!.receiverId!!.toByteString() +" " + it)
                //txInfoMap.getOrPut(it.hashData!!.receiverId!!.toByteString(), { mutableListOf() }).add(it)
                unspendMap.getOrPut(it.hashData!!.receiverId!!.toByteString(), { mutableListOf() }).add(outWithTx)
                txIndex.put(tx.hash!!.toByteString() + it.index, outWithTx)
            }
            tx.hashData?.inputs?.forEach { inp ->
                val oldOut = txIndex[inp.hashData!!.txHash!!.toByteString() + inp.index] ?: throw IllegalArgumentException("Input is not valid: $inp")
                val removed = unspendMap.getOrPut(oldOut.hashData!!.receiverId!!.toByteString(), { mutableListOf() }).remove(oldOut)
                println("remove " +oldOut.hashData!!.receiverId!!.toByteString() +" " + oldOut)
                if(!removed)throw IllegalArgumentException("Input $inp not valid")
            }
        }
    }
}


fun TxOutput.addTxHash(hash: ByteArray?): TxOutput{
    hashData = hashData!!.copy(txHash = hash)
    return this
}