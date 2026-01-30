package com.moomark.post.configuration.passport;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class PassportResponse {
  private String key;
  private String passport;
}
