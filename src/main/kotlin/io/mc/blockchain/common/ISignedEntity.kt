package io.mc.blockchain.common

import java.beans.Transient

/**
 * ralf on 28.09.17.
 */
interface ISignedEntity {
    val signature: ByteArray?
    val hash: ByteArray?
}

interface IHashedEntity {
    val hash: ByteArray?
    val hashData: ISignable?
}

interface ISignable {
    @Transient
    fun getSignedBytes(): ByteArray
}