package com.moomark.post.model.dto

data class CategoryDto(
    val id: Long? = null,
    val parentsId: Long = 0L,
    val categoryType: String? = null,
)
