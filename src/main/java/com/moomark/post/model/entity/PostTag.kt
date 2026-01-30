package com.moomark.post.model.entity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PostTag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "post_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;

  @JoinColumn(name = "comment_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Tag tag;

  @Builder
  public PostTag(Post post, Tag tag) {
    this.post = post;
    this.tag = tag;
  }
}
