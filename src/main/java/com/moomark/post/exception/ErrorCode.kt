package com.moomark.post.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  DONT_KNOW_ERROR("DOESN'T KNOW ERROR", 100000),
  CANNOT_FIND_POST("CANNOT_FIND_POSTINFO", 100001),
  CANNOT_FIND_CATEGORY("CANNOT_FIND_CATEGORYINFO", 100002),
  CANNOT_FIND_PARENT_CATEGORY("CANNOT_FIND_PARENT_CATEGORY", 100003),
  CANNOT_FIND_CHILD_CATEGORY("CANNOT_FIND_CHILD_CATEGORY", 100004);

  protected final String msg;
  protected final int code;
}
