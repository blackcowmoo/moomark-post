package com.moomark.post.repository

import com.moomark.post.model.entity.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PostRepository : JpaRepository<Post, Long> {
    fun findByTitleContaining(title: String): List<Post>

    fun findByUserId(userId: String): List<Post>

    override fun findById(id: Long): Optional<Post>

    fun findByIdGreaterThan(id: Long, paging: Pageable): List<Post>

    fun findByIdLessThan(id: Long, paging: Pageable): List<Post>

    fun findByUserIdAndIdLessThan(userId: String, id: Long, paging: Pageable): List<Post>

    override fun count(): Long

    fun countByUserId(userId: String): Long
}
