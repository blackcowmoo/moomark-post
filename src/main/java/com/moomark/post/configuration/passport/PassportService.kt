package com.moomark.post.configuration.passport;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

@Slf4j
@Service
public class PassportService {

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private PassportRepository passportRepository;

  private Base64.Decoder decoder = Base64.getDecoder();

  public User parsePassport(String passport, String passportKey) {
    try {
      Passport passportResult = decryptPassport(passportKey);
      if (passportResult.getExp().after(Timestamp.valueOf(LocalDateTime.now()))) {
        String hash = passportResult.getHash();
        SecretKey key = new SecretKeySpec(decoder.decode(passportResult.getKey()), "AES");
        String userBody = passportRepository.aesDecrypt(decoder.decode(passport), key);
        if (getHash(userBody).equals(hash)) {
          return mapper.readValue(decoder.decode(userBody), User.class);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return null;
  }

  private Passport decryptPassport(String passport) throws Exception {
    return mapper.readValue(passportRepository.rsaDecryptByPublicKey(decoder.decode(passport)), Passport.class);
  }

  private String getHash(String user) throws Exception {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] digest = md.digest(user.getBytes(StandardCharsets.UTF_8));
    return DatatypeConverter.printHexBinary(digest);
  }

}
