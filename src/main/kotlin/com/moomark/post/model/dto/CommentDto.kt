package com.moomark.post.model.dto

data class CommentDto(
    val id: Long? = null,
    val userId: String? = null,
    val postId: Long? = null,
    val parentsId: Long? = null,
    val childIdList: List<Long>? = null,
    val content: String? = null,
)
