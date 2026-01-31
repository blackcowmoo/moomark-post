package com.moomark.post.configuration

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets
import java.time.Duration

@Configuration
class RestTemplateConfig {
    companion object {
        private const val TIMEOUT_MILLIS = 5000L
    }

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate =
        restTemplateBuilder
            .requestFactory {
                BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory())
            }.setConnectTimeout(Duration.ofMillis(TIMEOUT_MILLIS))
            .setReadTimeout(Duration.ofMillis(TIMEOUT_MILLIS))
            .additionalMessageConverters(StringHttpMessageConverter(StandardCharsets.UTF_8))
            .build()
}
