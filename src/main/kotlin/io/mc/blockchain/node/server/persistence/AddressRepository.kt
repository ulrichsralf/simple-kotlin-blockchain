package io.mc.blockchain.node.server.persistence

import io.mc.blockchain.common.Address
import io.mc.blockchain.node.server.utils.toBase64String
import org.springframework.stereotype.Component


/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class AddressRepository {

    val map = mutableMapOf<String, Address>()


    fun findAll(): List<Address> {
        return map.values.toList()
    }


    fun save(address: Address) {
        map.put(address.id!!.toBase64String(), address)
    }

    fun delete(id: ByteArray) {
        map.remove(id.toBase64String())
    }

    fun exists(id: ByteArray): Boolean {
        return map.containsKey(id.toBase64String())
    }

    fun findOne(senderId: ByteArray): Address? {
        return map.get(senderId.toBase64String())
    }

}