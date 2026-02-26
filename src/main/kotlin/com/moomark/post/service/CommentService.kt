package com.moomark.post.service

import com.moomark.post.model.dto.CommentDto
import com.moomark.post.model.entity.Comment
import com.moomark.post.repository.CommentRepository
import com.moomark.post.repository.PostCommentRepository
import com.moomark.post.repository.PostRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentService(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val postCommentRepository: PostCommentRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val PAGE_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun getCommentByPostId(postId: Long): List<CommentDto> {
        val post =
            postRepository
                .findById(postId)
                .orElseThrow { Exception("No comment information was found by post id.") }

        return postCommentRepository.findByPost(post).map { postComment ->
            postComment.comment.toDto()
        }
    }

    @Transactional(readOnly = true)
    fun getCommentByPostId(postId: Long, pageNumber: Int): List<CommentDto> {
        val post =
            postRepository
                .findById(postId)
                .orElseThrow { Exception("No comment information was found by post id.") }

        val pageRequest = PageRequest.of(pageNumber, PAGE_SIZE)
        val postCommentPage = postCommentRepository.findByPost(post, pageRequest)

        return postCommentPage.content.map { it.comment.toDto() }
    }

    @Transactional(readOnly = true)
    fun getCommentCountByPostId(postId: Long): Int {
        val post =
            postRepository
                .findById(postId)
                .orElseThrow { Exception("No post information was found by post id.") }

        return postCommentRepository.findByPost(post).size
    }

    @Transactional(readOnly = true)
    fun getCommentByUserId(postId: Long, userId: String): List<CommentDto> {
        val post =
            postRepository
                .findById(postId)
                .orElseThrow { Exception("No comment information was found by post id") }

        return postCommentRepository
            .findByPost(post)
            .map { it.comment }
            .filter { it.userId == userId }
            .map { it.toDto() }
    }

    fun saveComment(commentDto: CommentDto): Long {
        val comment =
            Comment(
                content = commentDto.content,
                userId = commentDto.userId,
            )
        return commentRepository.save(comment).id!!
    }

    fun deleteComment(id: Long): Boolean = try {
        val comment =
            commentRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("No comment information was found by comment id.") }
        commentRepository.deleteById(comment.id!!)
        true
    } catch (e: NoSuchElementException) {
        log.error("delete error {}", e.message)
        false
    }

    /**
     * Entity를 DTO로 변환하는 확장 함수 (코드 중복 제거)
     */
    private fun Comment.toDto() = CommentDto(
        id = this.id,
        content = this.content,
        parentsId = this.parent?.id,
        userId = this.userId,
    )
}
