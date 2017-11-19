package io.mc.blockchain.common

import java.util.*


data class Address(
        val id: ByteArray,
        val publicKey: ByteArray) {

    override fun equals(other: Any?) = this === other
            || other is Address && Arrays.equals(id, other.id)

    override fun hashCode() = id.hashCode()

}
