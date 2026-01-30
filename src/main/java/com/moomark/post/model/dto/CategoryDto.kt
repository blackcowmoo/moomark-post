package com.moomark.post.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
  private Long id;

  @Builder.Default
  private Long parentsId = 0L;

  private String categoryType;
}
