package io.mc.blockchain.node.server.persistence

import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import org.springframework.stereotype.Component


/**
 * @author Ralf Ulrich
 * 27.08.17
 */
@Component
class AddressRepository {

    val map: HTreeMap<String, Any>

    init {
        map = DBMaker.memoryDB().make().hashMap("address", Serializer.STRING, Serializer.JAVA).create()
    }


    fun findAll(): List<Address> {
        return map.values.filterIsInstance(Address::class.java)
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
        return map.get(senderId) as Address?
    }

}