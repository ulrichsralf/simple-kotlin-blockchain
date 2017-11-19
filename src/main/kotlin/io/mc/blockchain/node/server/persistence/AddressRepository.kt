package io.mc.blockchain.node.server.persistence

import io.mc.blockchain.common.Address
import io.mc.blockchain.node.server.utils.toByteString
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
        map.put(address.id.toByteString(), address)
    }

    fun delete(id: ByteArray) {
        map.remove(id.toByteString())
    }

    fun exists(id: ByteArray): Boolean {
        return map.containsKey(id.toByteString())
    }

    fun findOne(senderId: ByteArray): Address? {
        return map.get(senderId.toByteString())
    }

}