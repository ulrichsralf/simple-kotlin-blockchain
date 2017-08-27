package io.mc.blockchain.node.server.rest


import io.mc.blockchain.node.server.persistence.Address
import io.mc.blockchain.node.server.persistence.AddressRepository
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("address")
class AddressController @Autowired constructor(val addressRepository: AddressRepository) {

    val LOG = getLogger()

    @RequestMapping
    fun addresses(): Collection<Address> {
        return addressRepository.findAll().toList()
    }

    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    fun addAddress(@RequestBody address: Address, response: HttpServletResponse) {
        LOG.info("Add address " + address.id)
        if (addressRepository.exists(address.id)) {
            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
        } else {
            addressRepository.save(address)
            response.status = HttpServletResponse.SC_ACCEPTED
        }
    }


}
