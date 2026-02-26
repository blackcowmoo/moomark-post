@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.moomark.post.controller

import com.moomark.post.configuration.passport.User
import com.moomark.post.exception.JpaException
import com.moomark.post.model.dto.CommentDto
import com.moomark.post.model.dto.PostDto
import com.moomark.post.model.entity.Post
import com.moomark.post.service.CommentService
import com.moomark.post.service.PostService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
class PostController(
    private val postService: PostService,
    private val commentService: CommentService,
) {
    // Data classes
    data class RequestTotalPostInfo(
        val postInfo: PostDto,
        val totalCommentCount: Int,
        val commentList: List<CommentDto>,
    )

    companion object {
        private val log = LoggerFactory.getLogger(PostController::class.java)
        private const val MAX_TITLE_LENGTH = 255
        private const val MAX_CONTENT_LENGTH = 10000
    }

    private fun getUser(): User? = SecurityContextHolder.getContext().authentication.principal as? User

    private fun sanitizeInput(input: String?): String? {
        if (input == null) return null
        return input
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;")
            .trim()
    }

    private fun validateInput(title: String?, content: String?): String? {
        val errors = mutableListOf<String>()

        if (title.isNullOrEmpty() || content.isNullOrEmpty()) {
            errors.add("Title and content cannot be empty")
        }

        if ((title?.length ?: 0) > MAX_TITLE_LENGTH) {
            errors.add("Title exceeds maximum length of $MAX_TITLE_LENGTH")
        }

        if ((content?.length ?: 0) > MAX_CONTENT_LENGTH) {
            errors.add("Content exceeds maximum length of $MAX_CONTENT_LENGTH")
        }

        return if (errors.isNotEmpty()) errors.joinToString("; ") else null
    }

    @GetMapping("/api/v1/posts")
    fun getPosts(
        @RequestParam(required = false) offset: Long?,
        @RequestParam(required = false) limit: Int?,
    ): List<Post> = postService.getPosts(offset, limit)

    @GetMapping("/api/v1/posts/count")
    fun getPostsCount(): Long = postService.getPostsCount()

    @GetMapping("/api/v1/post/{postId}")
    fun getPost(@PathVariable postId: Long): ResponseEntity<Post> = try {
        ResponseEntity(postService.getPost(postId), HttpStatus.OK)
    } catch (e: JpaException) {
        log.debug("Post not found: postId=$postId", e)
        ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping("/post/{postId}/content")
    @Throws(JpaException::class)
    fun getPostInfoById(@PathVariable postId: Long): ResponseEntity<PostDto> =
        ResponseEntity(postService.getPostInfoById(postId), HttpStatus.OK)

    @GetMapping("/post/{postId}/info")
    @Throws(Exception::class)
    fun getTotalPostInfoById(@PathVariable postId: Long): ResponseEntity<RequestTotalPostInfo> {
        val result =
            RequestTotalPostInfo(
                postInfo = postService.getPostInfoById(postId),
                totalCommentCount = commentService.getCommentCountByPostId(postId),
                commentList = commentService.getCommentByPostId(postId),
            )
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/api/v1/post")
    fun writePost(response: HttpServletResponse, @RequestBody(required = true) body: PostDto): ResponseEntity<*> {
        val user = getUser()
        val validationError = validateInput(body.title, body.content)

        val errorResponse =
            when {
                user == null -> {
                    response.status = HttpStatus.UNAUTHORIZED.value()
                    ResponseEntity(mapOf("error" to "Unauthorized"), HttpStatus.UNAUTHORIZED)
                }

                validationError != null -> {
                    response.status = HttpStatus.BAD_REQUEST.value()
                    ResponseEntity(
                        mapOf("error" to validationError),
                        HttpStatus.BAD_REQUEST,
                    )
                }

                else -> {
                    null
                }
            }

        return (
            errorResponse ?: ResponseEntity(
                postService.savePost(
                    user!!.userId,
                    sanitizeInput(body.title),
                    sanitizeInput(body.content),
                ),
                HttpStatus.OK,
            )
            )
    }

    @DeleteMapping("/post/{postId}")
    @Throws(JpaException::class)
    fun deletePostInfoById(@PathVariable postId: Long): ResponseEntity<String> {
        postService.deletePost(postId)
        return ResponseEntity("Success to delete post information", HttpStatus.OK)
    }
}
