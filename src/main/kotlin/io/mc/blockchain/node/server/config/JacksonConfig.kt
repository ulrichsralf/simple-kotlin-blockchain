package io.mc.blockchain.node.server.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mc.blockchain.node.server.utils.ByteArrayDeserializer
import io.mc.blockchain.node.server.utils.ByteArraySerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import kotlin.reflect.KClass


/**
 * ralf on 03.10.17.
 */
@Configuration
class JacksonConfig : WebMvcConfigurationSupport() {

    @Bean
    fun customJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val jsonConverter = MappingJackson2HttpMessageConverter()
        jsonConverter.objectMapper = objectMapper
        return jsonConverter
    }


    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(0, customJackson2HttpMessageConverter())
    }

    companion object {

        val objectMapper = jacksonObjectMapper()
                .registerModule(KotlinModule())
                .registerModule(getBase64Module())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        fun getBase64Module(): Module {
            return SimpleModule("Base64Url").apply {
                addSerializer(ByteArray::class.java, ByteArraySerializer())
                addDeserializer(ByteArray::class.java, ByteArrayDeserializer())
            }
        }
    }
}


fun Any.toJsonString(): String {
    return JacksonConfig.objectMapper.writeValueAsString(this)
}

fun <T : Any> String.parseJson(type: KClass<T>): T {
    return JacksonConfig.objectMapper.readValue(this, type.java)
}

