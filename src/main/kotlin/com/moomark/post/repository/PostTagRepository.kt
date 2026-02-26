package com.moomark.post.repository

import com.moomark.post.model.entity.PostTag
import org.springframework.data.jpa.repository.JpaRepository

interface PostTagRepository : JpaRepository<PostTag, Long>
