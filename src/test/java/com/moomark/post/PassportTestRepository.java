package com.moomark.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moomark.post.configuration.passport.PassportResponse;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Repository
public class PassportTestRepository {
  private static class Token {
    public String token;
  }

  @Value("${passport.auth-server.google-login}")
  private String loginEndpoint;

  @Value("${passport.auth-server.generate-passport}")
  private String generatePassportEndpoint;

  @Value("${passport.auth-server.withdraw}")
  private String withdrawEndpoint;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper mapper;

  private String userId = null;

  @PostConstruct
  public void generateUserId() {
    Random random = new Random();
    userId = String.valueOf(random.nextInt(Integer.MAX_VALUE));
  }

//  @Override
//  protected void finalize() {
//    withdrawUser();
//  }

  public String loginUser() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(loginEndpoint)
        .queryParam("code", "test-" + userId);

    Token tokens = restTemplate
        .exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(null, null), Token.class).getBody();
    return tokens.token;
  }

  public PassportResponse generatePassport() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", loginUser());
    try {
      return mapper.readValue(
          restTemplate.exchange(
              generatePassportEndpoint, HttpMethod.GET, new HttpEntity<>(headers), String.class
          ).getBody(),
          PassportResponse.class
      );
    } catch (Exception e) {
      return null;
    }
  }

  private void withdrawUser() {
    HttpHeaders headers = new HttpHeaders();
    PassportResponse passport = generatePassport();
    headers.set("x-moom-passport-user", passport.getPassport());
    headers.set("x-moom-passport-key", passport.getKey());

    restTemplate.exchange(withdrawEndpoint, HttpMethod.DELETE, new HttpEntity<>(headers), void.class);
  }
}
