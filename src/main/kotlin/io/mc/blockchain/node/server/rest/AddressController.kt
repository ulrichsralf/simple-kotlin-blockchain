package io.mc.blockchain.node.server.rest


import io.mc.blockchain.common.Address
import io.mc.blockchain.node.server.exceptions.ResourceNotFound
import io.mc.blockchain.node.server.persistence.AddressRepository
import io.mc.blockchain.node.server.utils.fromByteString
import io.mc.blockchain.node.server.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("address")
class AddressController @Autowired constructor(val addressRepository: AddressRepository) {

    val LOG = getLogger()

    @RequestMapping
    fun addresses(): Collection<Address> {
        return addressRepository.findAll().toList()
    }


    @RequestMapping("/{id}")
    fun addresse(@PathVariable("id") id: String): Address? {
        return try {
            addressRepository.findOne(id.fromByteString()) ?: throw ResourceNotFound()
        } catch (e: IllegalArgumentException) {
            throw ResourceNotFound()
        }

    }




    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    fun addAddress(@RequestBody address: Address, response: HttpServletResponse) {
        LOG.info("Add address " + address.id)
        if (addressRepository.exists(address.id!!)) {
            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
        } else {
            addressRepository.save(address)
            response.status = HttpServletResponse.SC_ACCEPTED
        }
    }


}
