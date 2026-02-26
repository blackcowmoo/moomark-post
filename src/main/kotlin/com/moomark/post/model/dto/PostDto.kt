package com.moomark.post.model.dto

import java.time.LocalDateTime

data class PostDto(
    val id: Long? = null,
    val userId: String? = null,
    val recommendCount: Long? = null,
    val viewsCount: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val uploadTime: LocalDateTime? = null,
    val categories: List<CategoryDto>? = null,
)
