package com.moomark.post.configuration.passport

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Base64
import javax.crypto.spec.SecretKeySpec

@Service
class PassportService(
    private val mapper: ObjectMapper,
    private val passportRepository: PassportRepository,
) {
    private val decoder: Base64.Decoder = Base64.getDecoder()

    companion object {
        private val log = LoggerFactory.getLogger(PassportService::class.java)
    }

    fun parsePassport(passport: String, passportKey: String): User? = try {
        val passportResult = decryptPassport(passportKey)
        if (passportResult.exp?.after(Timestamp.valueOf(LocalDateTime.now())) == true) {
            val hash = passportResult.hash
            val key = SecretKeySpec(decoder.decode(passportResult.key), "AES")
            val userBody = passportRepository.aesDecrypt(decoder.decode(passport), key)
            if (getHash(userBody) == hash) {
                mapper.readValue(decoder.decode(userBody), User::class.java)
            } else {
                null
            }
        } else {
            null
        }
    } catch (e: IllegalArgumentException) {
        log.error("Invalid passport format: ${e.message}", e)
        null
    } catch (e: com.fasterxml.jackson.core.JsonProcessingException) {
        log.error("Invalid passport JSON: ${e.message}", e)
        null
    } catch (e: IllegalStateException) {
        log.error("Invalid passport state: ${e.message}", e)
        null
    }

    @Throws(Exception::class)
    private fun decryptPassport(passport: String): Passport {
        val decrypted =
            passportRepository.rsaDecryptByPublicKey(decoder.decode(passport)) ?: run {
                check(false) { "Failed to decrypt passport" }
                ""
            }
        return mapper.readValue(decrypted, Passport::class.java)
    }

    @Throws(Exception::class)
    private fun getHash(user: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(user.toByteArray(StandardCharsets.UTF_8))
        return digest.joinToString("") { "%02X".format(it) }
    }
}
