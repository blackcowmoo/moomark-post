package com.moomark.post.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moomark.post.configuration.passport.User;
import com.moomark.post.exception.JpaException;
import com.moomark.post.model.dto.CommentDto;
import com.moomark.post.model.dto.PostDto;
import com.moomark.post.model.entity.Post;
import com.moomark.post.service.PostService;
import com.moomark.post.service.CommentService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;
  private final CommentService commentService;

  private User getUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  /*
   * =================================== GET ===================================
   */
  @GetMapping("/api/v1/posts")
  public List<Post> getPosts(@RequestParam(required = false) Long offset,
      @RequestParam(required = false) Integer limit) {
    return postService.getPosts(offset, limit);
  }

  @GetMapping("/api/v1/posts/count")
  public long getPostsCount() {
    return postService.getPostsCount();
  }

  @GetMapping("/api/v1/post/{postId}")
  public Post getPost(@PathVariable("postId") Long postId) {
    return postService.getPost(postId);
  }

  @GetMapping("/post/{postId}/content")
  public ResponseEntity<PostDto> getPostInfoById(@PathVariable("postId") Long postId)
      throws JpaException {
    return new ResponseEntity<>(postService.getPostInfoById(postId), HttpStatus.OK);
  }

  @GetMapping("/post/{postId}/info")
  public ResponseEntity<RequestTotalPostInfo> getTotalPostInfoById(
      @PathVariable("postId") Long postId) throws Exception {
    RequestTotalPostInfo result = new RequestTotalPostInfo();
    result.setPostInfo(postService.getPostInfoById(postId));
    result.setTotalCommentCount(commentService.getCommentCountByPostId(postId));
    result.setCommentList(commentService.getCommentByPostId(postId));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /*
   * =================================== POST ==================================
   */
  @PostMapping("/api/v1/post")
  public Post writePost(@RequestHeader HttpHeaders headers, HttpServletResponse response,
      @RequestBody(required = true) PostDto body) {
    User user = getUser();
    if (user == null) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      return null;
    }

    String userId = user.getUserId();
    if (body.getTitle().equals("") || body.getContent().equals("")) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      return null;
    }

    return postService.savePost(userId, body.getTitle(), body.getContent());
  }

  @DeleteMapping("/post/{postId}")
  public ResponseEntity<String> deletePostInfoById(@PathVariable("postId") Long postId)
      throws JpaException {
    postService.deletePost(postId);

    return new ResponseEntity<>("Success to delete post information", HttpStatus.OK);
  }

  /*
   * ============================= Static class ================================
   */
  @Data
  private class RequestTotalPostInfo {
    PostDto postInfo;
    int totalCommentCount;
    List<CommentDto> commentList;
  }
}
