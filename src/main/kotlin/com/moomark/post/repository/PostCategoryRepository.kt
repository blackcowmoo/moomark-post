package com.moomark.post.repository

import com.moomark.post.model.entity.Category
import com.moomark.post.model.entity.Post
import com.moomark.post.model.entity.PostCategory
import org.springframework.data.jpa.repository.JpaRepository

interface PostCategoryRepository : JpaRepository<PostCategory?, Long?> {
    fun findByPost(post: Post?): MutableList<PostCategory?>?

    fun findByCategory(category: Category?): MutableList<PostCategory?>?

    fun findByPostAndCategory(post: Post?, category: Category?): PostCategory?
}
