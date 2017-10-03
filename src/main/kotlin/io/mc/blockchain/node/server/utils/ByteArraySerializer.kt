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
class ByteArraySerializer : StdSerializer<ByteArray>(ByteArray::class.java) {
    override fun serialize(value: ByteArray, jgen: JsonGenerator, provider: SerializerProvider?) {
        jgen.writeString(value.toByteString())
    }
}

class ByteArrayDeserializer : StdDeserializer<ByteArray>(ByteArray::class.java) {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext?): ByteArray {
        return parser.readValueAs(String::class.java).fromByteString()
    }
}