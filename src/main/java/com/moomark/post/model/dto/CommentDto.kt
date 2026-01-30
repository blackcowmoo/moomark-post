package com.moomark.post.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {
  private Long id;
  private String userId;
  private Long postId;
  private Long parentsId;
  private List<Long> childIdList;
  private String content;
}
