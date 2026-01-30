package com.moomark.post.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDto {
  private Long id;
  
  private String userId;
  
  private Long recommendCount;
  
  private Long viewsCount;
  
  private String title;
  
  private String content;
  
  private LocalDateTime uploadTime;
  
  private List<CategoryDto> categories;
}
