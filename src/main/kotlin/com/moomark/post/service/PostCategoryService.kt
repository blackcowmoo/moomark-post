package com.moomark.post.service

import com.moomark.post.exception.ErrorCode
import com.moomark.post.exception.JpaException
import com.moomark.post.model.dto.PostDto
import com.moomark.post.model.entity.Post
import com.moomark.post.model.entity.PostCategory
import com.moomark.post.repository.CategoryRepository
import com.moomark.post.repository.PostCategoryRepository
import com.moomark.post.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostCategoryService(
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository,
    private val postCategoryRepository: PostCategoryRepository
) {
    fun addCategoryToPost(
        postId: Long,
        categoryId: Long
    ) {
        val post =
            postRepository.findById(postId).orElseThrow {
                JpaException(ErrorCode.CANNOT_FIND_POST.msg)
            }
        val category =
            categoryRepository.findById(categoryId).orElseThrow {
                JpaException(
                    ErrorCode.CANNOT_FIND_CATEGORY.msg,
                    ErrorCode.CANNOT_FIND_CATEGORY.code
                )
            }

        postCategoryRepository.save(
            PostCategory(post = post, category = category)
        )
    }

    fun deleteCategoryToPost(
        postId: Long,
        categoryId: Long
    ) {
        val post =
            postRepository.findById(postId).orElseThrow {
                JpaException(ErrorCode.CANNOT_FIND_POST.msg, ErrorCode.CANNOT_FIND_POST.code)
            }
        val category =
            categoryRepository.findById(categoryId).orElseThrow {
                JpaException(
                    ErrorCode.CANNOT_FIND_CATEGORY.msg,
                    ErrorCode.CANNOT_FIND_CATEGORY.code
                )
            }

        postCategoryRepository.findByPostAndCategory(post, category)?.let { postCategory ->
            postCategoryRepository.delete(postCategory)
        }
    }

    @Transactional(readOnly = true)
    fun getPostListByCategory(categoryId: Long): List<PostDto> {
        val category =
            categoryRepository.findById(categoryId).orElseThrow {
                JpaException(
                    ErrorCode.CANNOT_FIND_CATEGORY.msg,
                    ErrorCode.CANNOT_FIND_CATEGORY.code
                )
            }

        return postCategoryRepository.findByCategory(category)?.map { postCategory ->
            postCategory?.post!!.toDto()
        } ?: emptyList()
    }

    // DTO 변환 확장 함수
    private fun Post.toDto() =
        PostDto(
            id = this.id,
            userId = this.userId,
            title = this.title,
            content = this.content,
            uploadTime = this.uploadTime,
            recommendCount = this.recommendCount,
            viewsCount = this.viewsCount
        )
}
