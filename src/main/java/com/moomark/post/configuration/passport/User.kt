package com.moomark.post.configuration.passport;

public class User {
  public String id;
  public String email;
  public String nickname;
  public String picture;
  public String authProvider;

  public String getUserId() {
    return authProvider + "@" + id;
  }
}
