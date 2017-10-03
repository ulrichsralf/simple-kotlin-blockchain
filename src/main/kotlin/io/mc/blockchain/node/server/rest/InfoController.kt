package io.mc.blockchain.node.server.rest

import io.mc.blockchain.common.TxData
import io.mc.blockchain.node.server.persistence.InputOutputRepository
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("info")
class InfoController @Autowired
constructor(val inputOutputRepository: InputOutputRepository) {

    val LOG = getLogger()

    @RequestMapping
    fun transactionPool(id: String): List<TxData> {
        return inputOutputRepository.findAll(id)
    }


}