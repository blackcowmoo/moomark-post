package com.moomark.post.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "comment")
  private List<PostComment> post;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "content")
  private String content;

  @JoinColumn(name = "parent_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Comment parent;

  @OneToMany(mappedBy = "parent")
  private List<Comment> childList;

  @Builder
  public Comment(String userId, String content) {
    this.userId = userId;
    this.content = content;
  }

  public Long getParentId() {
    return Optional.ofNullable(this.parent)
        .map(Comment::getId)
        .orElse((long) 0);
  }

  public List<Long> getChildIdList() {
    List<Long> result = new ArrayList<>();
    for (Comment child : this.childList) {
      result.add(child.getId());
    }
    return result;
  }
}
