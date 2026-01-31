package com.moomark.post.configuration.passport

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.Cipher
import javax.crypto.SecretKey

@Repository
class PassportRepository(
    private val restTemplate: RestTemplate,
) {
    @Value("\${passport.auth-server.public-key}")
    private lateinit var apiEndpoint: String

    private lateinit var publicKeyString: String
    private lateinit var publicKey: PublicKey
    private lateinit var cipher: Cipher

    companion object {
        private val log = LoggerFactory.getLogger(PassportRepository::class.java)
    }

    @Throws(Exception::class)
    @PostConstruct
    fun getPassportPublicKey() {
        publicKeyString = restTemplate
            .exchange(
                apiEndpoint,
                HttpMethod.GET,
                HttpEntity<Any?>(null, null),
                String::class.java,
            ).body ?: run {
            check(false) { "Failed to fetch public key" }
            ""
        }

        val keyFactory = KeyFactory.getInstance("RSA")
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        val ukeySpec = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString))
        publicKey = keyFactory.generatePublic(ukeySpec)
    }

    fun rsaDecryptByPublicKey(data: ByteArray): String? = try {
        cipher?.init(Cipher.DECRYPT_MODE, publicKey)
        String(cipher.doFinal(data))
    } catch (e: IllegalStateException) {
        log.error("decryptByPublicKey: ", e)
        null
    }

    @Throws(Exception::class)
    fun aesDecrypt(body: ByteArray, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decrypted = cipher.doFinal(body)
        return String(decrypted)
    }
}
