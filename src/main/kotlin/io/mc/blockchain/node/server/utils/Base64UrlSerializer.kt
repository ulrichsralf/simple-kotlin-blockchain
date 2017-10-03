package io.mc.blockchain.node.server.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * ralf on 03.10.17.
 */
class Base64UrlSerializer : StdSerializer<ByteArray>(ByteArray::class.java) {
    override fun serialize(value: ByteArray, jgen: JsonGenerator, provider: SerializerProvider?) {
        jgen.writeString(value.toBase64String())
    }
}

class Base64UrlDeserializer : StdDeserializer<ByteArray>(ByteArray::class.java) {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext?): ByteArray {
        return parser.readValueAs(String::class.java).fromBase64String()
    }
}