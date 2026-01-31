package com.moomark.post.service

import com.moomark.post.exception.ErrorCode
import com.moomark.post.exception.JpaException
import com.moomark.post.model.dto.CategoryDto
import com.moomark.post.model.entity.Category
import com.moomark.post.repository.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true) // 읽기 전용 기본 설정
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {
    fun getCategoryById(id: Long): CategoryDto {
        val category =
            categoryRepository.findById(id).orElseThrow {
                JpaException(
                    ErrorCode.CANNOT_FIND_CATEGORY.msg,
                    ErrorCode.CANNOT_FIND_CATEGORY.code,
                )
            }

        // Kotlin에서는 Builder 대신 named arguments를 주로 사용합니다.
        return CategoryDto(
            id = category.id,
            categoryType = category.categoryType,
            parentsId = category.getParentAfterNullCheck(),
        )
    }

    @Transactional
    fun addCategory(information: String): Long {
        val category =
            Category(
                categoryType = information,
            )

        return categoryRepository.save(category).id!!
    }

    @Transactional
    fun updateCategory(categoryId: Long, information: String) {
        val category =
            categoryRepository.findById(categoryId).orElseThrow {
                JpaException(
                    ErrorCode.CANNOT_FIND_CATEGORY.msg,
                    ErrorCode.CANNOT_FIND_CATEGORY.code,
                )
            }

        category.updateCategoryInfo(information)
    }

    @Transactional
    fun deleteCategory(id: Long) {
        categoryRepository.deleteById(id)
    }

    @Transactional
    fun addChildCategory(parentId: Long, childId: Long) {
        val parentCategory =
            categoryRepository.findById(parentId).orElseThrow {
                JpaException(
                    ErrorCode.CANNOT_FIND_PARENT_CATEGORY.msg,
                    ErrorCode.CANNOT_FIND_PARENT_CATEGORY.code,
                )
            }

        val childCategory =
            categoryRepository.findById(childId).orElseThrow {
                JpaException(
                    ErrorCode.CANNOT_FIND_CHILD_CATEGORY.msg,
                    ErrorCode.CANNOT_FIND_CHILD_CATEGORY.code,
                )
            }

        parentCategory.addChildCategory(childCategory)
    }

    @Transactional
    fun deleteChildCategory(parentId: Long, childId: Long): Boolean {
        val parentCategory =
            categoryRepository.findById(parentId).orElseThrow {
                JpaException(
                    ErrorCode.CANNOT_FIND_PARENT_CATEGORY.msg,
                    ErrorCode.CANNOT_FIND_PARENT_CATEGORY.code,
                )
            }

        val childCategory =
            categoryRepository.findById(childId).orElseThrow {
                JpaException(
                    ErrorCode.CANNOT_FIND_CHILD_CATEGORY.msg,
                    ErrorCode.CANNOT_FIND_CHILD_CATEGORY.code,
                )
            }

        parentCategory.removeChildCategory(childCategory)
        return true
    }
}
