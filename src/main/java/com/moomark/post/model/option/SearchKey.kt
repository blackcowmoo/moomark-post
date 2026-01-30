package com.moomark.post.model.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchKey {
  ID("id"), USER_ID("user_id");

  private final String key;
}
