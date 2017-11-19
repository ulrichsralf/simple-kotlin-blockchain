package io.mc.blockchain.node.server.rest

import io.mc.blockchain.common.TxOutput
import io.mc.blockchain.node.server.persistence.InputOutputRepository
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BalanceController @Autowired
constructor(val inputOutputRepository: InputOutputRepository) {

    val LOG = getLogger()

    @RequestMapping("balance/{id}")
    fun getBalance(@PathVariable("id") id: String): Map<String, Long> {
        return inputOutputRepository.findUnspend(id).map { it.hashData.type to it.hashData.value }
                .fold(mutableMapOf(), { map, pair -> map.apply { compute(pair.first, { _, v -> (v ?: 0) + pair.second }) } })
    }

    @RequestMapping("unspend/{id}")
    fun getUnspend(@PathVariable("id") id: String): List<TxOutput> {
        return inputOutputRepository.findUnspend(id)
    }


}