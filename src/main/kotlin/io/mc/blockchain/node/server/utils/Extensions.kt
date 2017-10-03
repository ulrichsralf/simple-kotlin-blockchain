package io.mc.blockchain.node.server.utils

import com.google.common.io.BaseEncoding
import org.slf4j.LoggerFactory

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
fun Any.getLogger() = LoggerFactory.getLogger(javaClass)

fun ByteArray.toHexString(): String = BaseEncoding.base16().lowerCase().encode(this)
fun ByteArray.toBase64String(): String = BaseEncoding.base64()   .encode(this)
fun String.bytesFromHex(): ByteArray = BaseEncoding.base16().lowerCase().decode(this)
