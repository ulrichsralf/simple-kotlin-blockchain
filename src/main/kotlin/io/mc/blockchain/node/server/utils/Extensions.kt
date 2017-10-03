package io.mc.blockchain.node.server.utils

import com.google.common.io.BaseEncoding
import org.slf4j.LoggerFactory

/**
 * @author Ralf Ulrich
 * 27.08.17
 */
fun Any.getLogger() = LoggerFactory.getLogger(javaClass)

fun ByteArray.toBase64String(): String = BaseEncoding.base64Url().encode(this)
fun String.fromBase64String(): ByteArray = BaseEncoding.base64Url().decode(this)
