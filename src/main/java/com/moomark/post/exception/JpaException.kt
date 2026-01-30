package com.moomark.post.exception;

public class JpaException extends Exception {
  private static final long serialVersionUID = 6786491966940496018L;
  private final int code;

  public JpaException(String msg, int code) {
    super(msg);
    this.code = code;
  }

  public JpaException(String msg) {
    super(msg);
    this.code = 100000;
  }

  public int getCode() {
    return code;
  }
}
