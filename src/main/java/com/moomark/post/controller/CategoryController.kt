package com.moomark.post.controller

import com.moomark.post.exception.JpaException
import com.moomark.post.model.dto.CategoryDto
import com.moomark.post.service.CategoryService
import com.moomark.post.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CategoryController(
    private val categoryService: CategoryService,
    private val postService: PostService
) {
    data class RequestCategoryInfo(
        val categoryId: Long,
        val categoryInfo: String
    )

    data class RequestChildCategory(
        val parentId: Long,
        val childId: Long
    )

    data class RequestAddCategoryToPost(
        val postId: Long,
        val categoryId: Long
    )

    @GetMapping("/category/{id}")
    @Throws(JpaException::class)
    fun getCategoryInfo(
        @PathVariable("id") categoryId: Long
    ): ResponseEntity<CategoryDto> = ResponseEntity(categoryService.getCategoryById(categoryId), HttpStatus.OK)

    @PostMapping("/category/{info}")
    fun addCategoryInfo(
        @PathVariable("info") categoryInfo: String
    ): Long = categoryService.addCategory(categoryInfo)

    @PostMapping("/category/child")
    @Throws(JpaException::class)
    fun addChildCategory(
        @RequestBody request: RequestChildCategory
    ): ResponseEntity<String> {
        categoryService.addChildCategory(request.parentId, request.childId)
        return ResponseEntity("Success to add child category", HttpStatus.OK)
    }

    @PostMapping("/category/mapping")
    @Throws(JpaException::class)
    fun addCategoryToPost(
        @RequestBody requestInformation: RequestAddCategoryToPost
    ) {
        postService.addCategoryToPost(
            requestInformation.postId,
            requestInformation.categoryId
        )
    }

    @PutMapping("/category/child")
    @Throws(JpaException::class)
    fun updateCategoryInfo(
        @RequestBody requestCategoryInfo: RequestCategoryInfo
    ) {
        categoryService.updateCategory(
            requestCategoryInfo.categoryId,
            requestCategoryInfo.categoryInfo
        )
    }

    @DeleteMapping("/category")
    @Throws(JpaException::class)
    fun deleteCategory(
        @RequestBody requestCategoryInfo: RequestCategoryInfo
    ) {
        categoryService.deleteCategory(requestCategoryInfo.categoryId)
    }

    @DeleteMapping("/category/child")
    @Throws(JpaException::class)
    fun deleteChildCategory(
        @RequestBody request: RequestChildCategory
    ) {
        categoryService.deleteChildCategory(request.parentId, request.childId)
    }
}
