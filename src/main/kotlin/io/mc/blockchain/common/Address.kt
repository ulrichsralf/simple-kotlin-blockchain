package io.mc.blockchain.common

import java.util.*


data class Address(
        var id: ByteArray? = null,
        var publicKey: ByteArray? = null) {

    override fun equals(other: Any?) = this === other
            || other is Address && Arrays.equals(id, other.id)
    override fun hashCode() = id!!.hashCode()

}
