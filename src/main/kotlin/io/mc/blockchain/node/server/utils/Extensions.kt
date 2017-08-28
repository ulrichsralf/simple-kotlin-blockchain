package io.mc.blockchain.node.server.utils

import com.google.common.io.BaseEncoding
import com.google.common.primitives.Longs
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.*

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
fun Any.getLogger() = LoggerFactory.getLogger(javaClass)

fun ByteArray.encodeBase64String() = BaseEncoding.base64().encode(this)
fun ByteArray.toHexString(): String = BaseEncoding.base16().encode(this)
fun String.bytesFromHex(): ByteArray = BaseEncoding.base16().decode(this)


fun UUID.toByteArray() = ByteBuffer.allocate(16).putLong(mostSignificantBits).putLong(leastSignificantBits).array()
fun Long.toByteArray() = Longs.toByteArray(this)
