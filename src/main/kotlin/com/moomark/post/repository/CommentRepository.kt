package com.moomark.post.repository

import com.moomark.post.model.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByUserId(userId: Long): Optional<Comment>
}
