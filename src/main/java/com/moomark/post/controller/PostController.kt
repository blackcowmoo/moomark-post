package com.moomark.post.controller

import com.moomark.post.configuration.passport.User
import com.moomark.post.exception.JpaException
import com.moomark.post.model.dto.CommentDto
import com.moomark.post.model.dto.PostDto
import com.moomark.post.model.entity.Post
import com.moomark.post.service.CommentService
import com.moomark.post.service.PostService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
class PostController(
    private val postService: PostService,
    private val commentService: CommentService
) {
    // Data classes
    data class RequestTotalPostInfo(
        val postInfo: PostDto,
        val totalCommentCount: Int,
        val commentList: List<CommentDto>
    )

    private fun getUser(): User? = SecurityContextHolder.getContext().authentication.principal as? User

    @GetMapping("/api/v1/posts")
    fun getPosts(
        @RequestParam(required = false) offset: Long?,
        @RequestParam(required = false) limit: Int?
    ): List<Post> = postService.getPosts(offset, limit)

    @GetMapping("/api/v1/posts/count")
    fun getPostsCount(): Long = postService.getPostsCount()

    @GetMapping("/api/v1/post/{postId}")
    fun getPost(
        @PathVariable("postId") postId: Long
    ): Post = postService.getPost(postId)

    @GetMapping("/post/{postId}/content")
    @Throws(JpaException::class)
    fun getPostInfoById(
        @PathVariable("postId") postId: Long
    ): ResponseEntity<PostDto> = ResponseEntity(postService.getPostInfoById(postId), HttpStatus.OK)

    @GetMapping("/post/{postId}/info")
    @Throws(Exception::class)
    fun getTotalPostInfoById(
        @PathVariable("postId") postId: Long
    ): ResponseEntity<RequestTotalPostInfo> {
        val result =
            RequestTotalPostInfo(
                postInfo = postService.getPostInfoById(postId),
                totalCommentCount = commentService.getCommentCountByPostId(postId),
                commentList = commentService.getCommentByPostId(postId)
            )
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/api/v1/post")
    fun writePost(
        @RequestHeader headers: HttpHeaders,
        response: HttpServletResponse,
        @RequestBody(required = true) body: PostDto
    ): Post? {
        val user = getUser()
        if (user == null) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return null
        }

        val userId = user.userId
        if (body.title?.isEmpty() == true || body.content?.isEmpty() == true) {
            response.status = HttpStatus.BAD_REQUEST.value()
            return null
        }

        return postService.savePost(userId, body.title, body.content)
    }

    @DeleteMapping("/post/{postId}")
    @Throws(JpaException::class)
    fun deletePostInfoById(
        @PathVariable("postId") postId: Long
    ): ResponseEntity<String> {
        postService.deletePost(postId)
        return ResponseEntity("Success to delete post information", HttpStatus.OK)
    }
}
