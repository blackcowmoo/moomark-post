package com.moomark.post.repository

import com.moomark.post.model.entity.Post
import com.moomark.post.model.entity.PostComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostCommentRepository : JpaRepository<PostComment, Long> {
    fun findByPost(post: Post): List<PostComment>

    fun findByPost(post: Post, pageable: Pageable): Page<PostComment>
}
