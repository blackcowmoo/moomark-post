package com.moomark.post.repository

import com.moomark.post.model.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByUserId(userId: String): List<Comment>
}
