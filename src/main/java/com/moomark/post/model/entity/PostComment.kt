package com.moomark.post.model.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
@EntityListeners(AuditingEntityListener::class)
class PostComment(
    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var post: Post,
    @JoinColumn(name = "comment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var comment: Comment
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
