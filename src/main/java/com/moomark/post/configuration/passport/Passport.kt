package com.moomark.post.configuration.passport;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Passport {
  public Timestamp exp; // expired timestamp
  public String key;
  public String hash;
}
