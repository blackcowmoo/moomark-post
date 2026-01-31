package com.moomark.post.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.moomark.post.configuration.passport.PassportResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.util.Random
import javax.annotation.PostConstruct

@Repository
class PassportTestRepository {
    private data class Token(
        val token: String,
    )

    @Value("\${passport.auth-server.google-login}")
    private lateinit var loginEndpoint: String

    @Value("\${passport.auth-server.generate-passport}")
    private lateinit var generatePassportEndpoint: String

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var mapper: ObjectMapper

    private var userId: String? = null

    companion object {
        private val log = LoggerFactory.getLogger(PassportTestRepository::class.java)
        private val SAFE_USERID_PATTERN = Regex("^[a-zA-Z0-9]+$")
        private const val MAX_USERID_LENGTH = 50
    }

    @PostConstruct
    fun generateUserId() {
        val random = Random()
        userId = random.nextInt(Int.MAX_VALUE).toString()
    }

    private fun sanitizeUserId(id: String?): String {
        if (id.isNullOrEmpty()) {
            return "default"
        }

        val trimmedId =
            if (id.length > MAX_USERID_LENGTH) {
                id.substring(0, MAX_USERID_LENGTH)
            } else {
                id
            }

        return if (SAFE_USERID_PATTERN.matches(trimmedId)) {
            trimmedId
        } else {
            trimmedId.replace(Regex("[^a-zA-Z0-9]"), "")
        }
    }

    fun loginUser(): String {
        val safeUserId = sanitizeUserId(userId)
        val builder =
            UriComponentsBuilder
                .fromHttpUrl(loginEndpoint)
                .queryParam("code", "test-$safeUserId")

        val tokens =
            restTemplate
                .exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    HttpEntity<Any?>(null, null),
                    Token::class.java,
                ).body
        return tokens?.token ?: ""
    }

    fun generatePassport(): PassportResponse? {
        val headers = HttpHeaders()
        headers.set("Authorization", loginUser())
        return try {
            val responseBody =
                restTemplate
                    .exchange(
                        generatePassportEndpoint,
                        HttpMethod.GET,
                        HttpEntity<Any?>(headers),
                        String::class.java,
                    ).body

            if (responseBody.isNullOrEmpty()) {
                log.error("Empty response body from passport generation endpoint")
                return null
            }

            mapper.readValue(responseBody, PassportResponse::class.java)
        } catch (e: IllegalArgumentException) {
            log.error("Invalid passport response format: {}", e.message)
            null
        } catch (e: com.fasterxml.jackson.core.JsonProcessingException) {
            log.error("JSON parsing error: {}", e.message)
            null
        } catch (e: org.springframework.web.client.RestClientException) {
            log.error("REST client error: {}", e.message)
            null
        }
    }
}
