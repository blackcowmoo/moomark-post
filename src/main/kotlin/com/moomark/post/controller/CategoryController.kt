package com.moomark.post.controller

import com.moomark.post.exception.JpaException
import com.moomark.post.model.dto.CategoryDto
import com.moomark.post.service.CategoryService
import com.moomark.post.service.PostCategoryService
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
    private val postCategoryService: PostCategoryService,
) {
    data class RequestCategoryInfo(
        val categoryId: Long,
        val categoryInfo: String,
    )

    data class RequestChildCategory(
        val parentId: Long,
        val childId: Long,
    )

    data class RequestAddCategoryToPost(
        val postId: Long,
        val categoryId: Long,
    )

    @GetMapping("/category/{id}")
    @Throws(JpaException::class)
    fun getCategoryInfo(@PathVariable("id") categoryId: Long): ResponseEntity<CategoryDto> =
        ResponseEntity(categoryService.getCategoryById(categoryId), HttpStatus.OK)

    @PostMapping("/category/{info}")
    fun addCategoryInfo(@PathVariable("info") categoryInfo: String): Long = categoryService.addCategory(categoryInfo)

    @PostMapping("/category/child")
    @Throws(JpaException::class)
    fun addChildCategory(@RequestBody request: RequestChildCategory): ResponseEntity<String> {
        categoryService.addChildCategory(request.parentId, request.childId)
        return ResponseEntity("Success to add child category", HttpStatus.OK)
    }

    @PostMapping("/category/mapping")
    @Throws(JpaException::class)
    fun addCategoryToPost(@RequestBody requestInformation: RequestAddCategoryToPost): ResponseEntity<String> {
        postCategoryService.addCategoryToPost(
            requestInformation.postId,
            requestInformation.categoryId,
        )
        return ResponseEntity("Success to add category to post", HttpStatus.OK)
    }

    @PutMapping("/category/child")
    @Throws(JpaException::class)
    fun updateCategoryInfo(@RequestBody requestCategoryInfo: RequestCategoryInfo): ResponseEntity<String> {
        categoryService.updateCategory(
            requestCategoryInfo.categoryId,
            requestCategoryInfo.categoryInfo,
        )
        return ResponseEntity("Success to update category info", HttpStatus.OK)
    }

    @DeleteMapping("/category")
    @Throws(JpaException::class)
    fun deleteCategory(@RequestBody requestCategoryInfo: RequestCategoryInfo): ResponseEntity<String> {
        categoryService.deleteCategory(requestCategoryInfo.categoryId)
        return ResponseEntity("Success to delete category", HttpStatus.OK)
    }

    @DeleteMapping("/category/child")
    @Throws(JpaException::class)
    fun deleteChildCategory(@RequestBody request: RequestChildCategory): ResponseEntity<String> {
        categoryService.deleteChildCategory(request.parentId, request.childId)
        return ResponseEntity("Success to delete child category", HttpStatus.OK)
    }
}
