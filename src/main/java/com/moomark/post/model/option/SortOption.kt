package com.moomark.post.model.option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SortOption {
  private SearchKey key;
  private Boolean asc;
}
