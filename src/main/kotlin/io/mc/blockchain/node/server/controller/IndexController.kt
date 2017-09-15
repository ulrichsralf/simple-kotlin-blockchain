package io.mc.blockchain.node.server.controller

import io.mc.blockchain.node.server.persistence.Transaction
import io.mc.blockchain.node.server.persistence.parseJson
import io.mc.blockchain.node.server.service.BlockService
import io.mc.blockchain.node.server.utils.bytesFromHex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

/**
 * @author Ralf Ulrich
 * 03.09.17
 */
@Controller
class IndexController @Autowired constructor(val blockService: BlockService) {

    @RequestMapping("/")
    fun getIndex(@RequestParam(required = false) index: Int?): ModelAndView {
        val blocks = blockService.getBlockchain()
        return ModelAndView("home", mutableMapOf("blocks" to blocks,
                "transactions" to if (index != null && index <= blocks.size) blocks[index - 1].transactions?.map { it.parseJson(Transaction::class) .apply { text = String(text!!.bytesFromHex()) } } else null))
    }

}