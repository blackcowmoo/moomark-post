package com.moomark.post.model.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "recommend_count")
  private Long recommendCount;

  @Column(name = "views_count")
  private Long viewsCount;

  @Column(name = "title")
  private String title;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Column(name = "upload_time")
  private LocalDateTime uploadTime;

  @Column(name = "category_id")
  @OneToMany(mappedBy = "post")
  private List<PostCategory> postCategory = new ArrayList<>();

  @Builder
  public Post(String userId, String title, String content) {
    this.userId = userId;
    this.title = title;
    this.content = content;
    this.recommendCount = 0L;
    this.viewsCount = 0L;
    this.uploadTime = LocalDateTime.now(ZoneOffset.UTC);
  }

  /* Function List */
  public void upCountViewCount() {
    this.viewsCount++;
  }

  public void downCountViewCount() {
    if (0 < this.viewsCount) {
      this.viewsCount--;
    }
  }

  public void updateInformation(String title, String content) {
    this.title = title;
    this.content = content;
    this.uploadTime = LocalDateTime.now();
  }
}
