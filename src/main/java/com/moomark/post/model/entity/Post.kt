package com.moomark.post.model.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
@EntityListeners(AuditingEntityListener::class)
class Post(
    @Column(name = "user_id")
    var userId: String,
    @Column(name = "title")
    var title: String,
    @Column(name = "content", columnDefinition = "TEXT")
    var content: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "recommend_count")
    var recommendCount: Long = 0L

    @Column(name = "views_count")
    var viewsCount: Long = 0L

    @Column(name = "upload_time")
    var uploadTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

    @Column(name = "category_id")
    @OneToMany(mappedBy = "post")
    var postCategory: MutableList<PostCategory> = mutableListOf()

    fun upCountViewCount() {
        viewsCount++
    }

    fun downCountViewCount() {
        if (viewsCount > 0) {
            viewsCount--
        }
    }

    fun updateInformation(
        title: String,
        content: String
    ) {
        this.title = title
        this.content = content
        this.uploadTime = LocalDateTime.now()
    }
}
