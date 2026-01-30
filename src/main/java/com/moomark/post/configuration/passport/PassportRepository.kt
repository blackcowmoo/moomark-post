package com.moomark.post.configuration.passport;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PassportRepository {
  @Value("${passport.auth-server.public-key}")
  private String apiEndpoint;

  private String publicKeyString;
  private PublicKey publicKey;
  private Cipher cipher;

  @Autowired
  private RestTemplate restTemplate;

  @PostConstruct
  public void getPassportPublicKey() throws Exception {
    publicKeyString = restTemplate.exchange(apiEndpoint, HttpMethod.GET, new HttpEntity<>(null, null), String.class)
        .getBody();
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
    publicKey = keyFactory.generatePublic(ukeySpec);
  }

  public String rsaDecryptByPublicKey(byte[] data) {
    try {
      cipher.init(Cipher.DECRYPT_MODE, publicKey);
      return new String(cipher.doFinal(data));
    } catch (Exception e) {
      log.error("decryptByPublicKey: ", e);
      return null;
    }
  }

  public String aesDecrypt(byte[] body, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decrypted = cipher.doFinal(body);
    return new String(decrypted);
  }
}
