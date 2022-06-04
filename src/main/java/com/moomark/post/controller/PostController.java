package com.moomark.post.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.moomark.post.domain.PostDto;
import com.moomark.post.domain.CommentDto;
import com.moomark.post.domain.Post;
import com.moomark.post.exception.JpaException;
import com.moomark.post.service.PostService;
import com.moomark.post.service.CommentService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;
  private final CommentService commentService;

  /*
   * =================================== GET ===================================
   */
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
  public Post writePost(@RequestHeader HttpHeaders headers, @RequestBody String title, @RequestBody String content,
      HttpServletResponse response) {
    List<String> userIdHeaders = headers.get("x-moom-user-id");
    if (userIdHeaders.size() != 1) {
      response.setStatus(401);
      return null;
    }

    String userId = userIdHeaders.get(0);
    return postService.savePost(userId, title, content);
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
