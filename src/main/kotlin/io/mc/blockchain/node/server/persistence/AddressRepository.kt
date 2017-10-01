package io.mc.blockchain.node.server.persistence

import io.mc.blockchain.common.Address
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
        map.put(address.id!!, address)
    }

    fun delete(id: String) {
        map.remove(id)
    }

    fun exists(id: String?): Boolean {
        return map.containsKey(id)
    }

    fun findOne(senderId: String?): Address? {
        return map.get(senderId)
    }

}