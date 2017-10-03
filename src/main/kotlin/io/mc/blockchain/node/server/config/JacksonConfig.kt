package io.mc.blockchain.node.server.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import io.mc.blockchain.node.server.utils.Base64UrlDeserializer
import io.mc.blockchain.node.server.utils.Base64UrlSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport


/**
 * ralf on 03.10.17.
 */
@Configuration
class JacksonConfig : WebMvcConfigurationSupport() {

    @Bean
    fun customJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val jsonConverter = MappingJackson2HttpMessageConverter()
        val objectMapper = ObjectMapper()

        objectMapper.registerModule(getBase64Module())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        jsonConverter.objectMapper = objectMapper
        return jsonConverter
    }


    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(0, customJackson2HttpMessageConverter())
    }

    companion object {
        fun getBase64Module(): Module {
            return SimpleModule("Base64Url").apply {
                addSerializer(ByteArray::class.java, Base64UrlSerializer())
                addDeserializer(ByteArray::class.java, Base64UrlDeserializer())
            }
        }
    }
}

