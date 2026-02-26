package com.moomark.post.service

import com.moomark.post.exception.ErrorCode
import com.moomark.post.exception.JpaException
import com.moomark.post.model.dto.PostDto
import com.moomark.post.model.entity.Post
import com.moomark.post.model.option.SearchKey
import com.moomark.post.model.option.SearchOption
import com.moomark.post.model.option.SortOption
import com.moomark.post.repository.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.Sort.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
) {
    companion object {
        private const val MAX_LIMIT = 100
    }

    fun savePost(postDto: PostDto): Post = savePost(postDto.userId!!, postDto.title!!, postDto.content!!)

    fun savePost(userId: String, title: String?, content: String?): Post = postRepository.save(
        Post(title = title, userId = userId, content = content),
    )

    @Transactional(readOnly = true)
    fun getPosts(offset: Long?, limit: Int?): List<Post> = getPostsWithOptions(offset, limit, null, null)

    @Transactional(readOnly = true)
    fun getPostsCount(): Long = getPostsCountWithOptions(null)

    @Transactional(readOnly = true)
    fun getPost(id: Long): Post = postRepository.findById(id).orElse(null)

    fun deletePost(postId: Long) {
        val post =
            postRepository.findById(postId).orElseThrow {
                JpaException(ErrorCode.CANNOT_FIND_POST.msg, ErrorCode.CANNOT_FIND_POST.code)
            }
        postRepository.delete(post)
    }

    fun getPostInfoById(id: Long): PostDto {
        val post =
            postRepository.findById(id).orElseThrow {
                JpaException(ErrorCode.CANNOT_FIND_POST.msg, ErrorCode.CANNOT_FIND_POST.code)
            }
        post.upCountViewCount() // 조회수 증가
        return post.toDto()
    }

    @Transactional(readOnly = true)
    fun getPostInfoByTitle(title: String): List<PostDto> =
        postRepository.findByTitleContaining(title).map { it.toDto() }

    private fun getPostsWithOptions(
        offset: Long?,
        limit: Int?,
        search: SearchOption?,
        order: SortOption?,
    ): List<Post> {
        val finalOffset = offset ?: Long.MAX_VALUE
        val finalLimit = if (limit == null || limit <= 0 || limit > MAX_LIMIT) MAX_LIMIT else limit

        val orders = mutableListOf<Order>()
        order?.let {
            orders.add(Order(if (it.asc) Direction.ASC else Direction.DESC, it.key.key))
        }
        orders.add(Order(Direction.DESC, SearchKey.ID.key))

        val pageRequest = PageRequest.of(0, finalLimit, Sort.by(orders))

        return when (search?.key) {
            SearchKey.USER_ID -> {
                postRepository.findByUserIdAndIdLessThan(
                    search.value,
                    finalOffset,
                    pageRequest,
                )
            }

            else -> {
                postRepository.findByIdLessThan(finalOffset, pageRequest)
            }
        }
    }

    private fun getPostsCountWithOptions(search: SearchOption?): Long = when (search?.key) {
        SearchKey.USER_ID -> postRepository.countByUserId(search.value)
        else -> postRepository.count()
    }

    // DTO 변환 확장 함수
    private fun Post.toDto() = PostDto(
        id = this.id,
        userId = this.userId,
        title = this.title,
        content = this.content,
        uploadTime = this.uploadTime,
        recommendCount = this.recommendCount,
        viewsCount = this.viewsCount,
    )
}
