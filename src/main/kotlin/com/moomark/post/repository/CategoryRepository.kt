package com.moomark.post.repository

import com.moomark.post.model.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByCategoryType(categoryType: String): Optional<Category>

    override fun findById(id: Long): Optional<Category>
}
