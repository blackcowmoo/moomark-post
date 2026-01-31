package com.moomark.post.model.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
@EntityListeners(AuditingEntityListener::class)
class Comment(
    @Column(name = "user_id")
    var userId: String?,
    @Column(name = "content")
    var content: String?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(mappedBy = "comment")
    var post: MutableList<PostComment> = mutableListOf()

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var parent: Comment? = null

    @OneToMany(mappedBy = "parent")
    var childList: MutableList<Comment> = mutableListOf()

    fun getParentId(): Long = parent?.id ?: 0L

    fun getChildIdList(): List<Long> = childList.mapNotNull { it.id }
}
