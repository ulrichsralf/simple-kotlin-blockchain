package io.mc.blockchain.node.server.persistence

import org.springframework.data.cassandra.mapping.PrimaryKey
import org.springframework.data.cassandra.mapping.Table
import java.util.*

@Table(value = "address")
data class Address(

        @PrimaryKey("id")
        var id: String? = null,

        /**
         * The public key for this Address to ensure everybody is able to verify signed messages
         */
        var publicKey: String? = null) {

    override fun equals(o: Any?) = this === o || o is Address && id == o.id
    override fun hashCode() = id!!.hashCode()

}
