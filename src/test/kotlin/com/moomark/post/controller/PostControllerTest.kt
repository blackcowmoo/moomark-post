package com.moomark.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.moomark.post.model.entity.Post
import com.moomark.post.repository.PassportTestRepository
import net.minidev.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var passportTestRepository: PassportTestRepository

    @Test
    @Order(1)
    fun getPostsCountBefore() {
        val posts =
            mvc
                .perform(get("/api/v1/posts/count"))
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString
                .toLong()

        assertEquals(posts, 0L)
    }

    @Test
    @Order(2)
    fun writePost() {
        val testTitle = "testTitle"
        val testContent = "testContent"

        val requestParams = JSONObject()
        requestParams["title"] = testTitle
        requestParams["content"] = testContent

        val passport = passportTestRepository.generatePassport()

        val post =
            mapper.readValue(
                mvc
                    .perform(
                        post("/api/v1/post")
                            .header("Content-Type", "application/json")
                            .header("x-moom-passport-user", passport!!.passport)
                            .header("x-moom-passport-key", passport!!.key)
                            .content(requestParams.toJSONString())
                    ).andExpect(status().isOk)
                    .andReturn()
                    .response
                    .contentAsString,
                Post::class.java
            )

        assertEquals(post.title, testTitle)
        assertEquals(post.content, testContent)
        assertEquals(post.recommendCount, 0L)
        assertEquals(post.viewsCount, 0L)
    }

    @Test
    @Order(3)
    fun getPostsCountAfter() {
        val posts =
            mvc
                .perform(get("/api/v1/posts/count"))
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString
                .toLong()

        assertEquals(posts, 1L)
    }

    @Test
    @Order(4)
    fun getPosts() {
        val post =
            mapper.readValue(
                mvc
                    .perform(get("/api/v1/posts").queryParam("limit", "1"))
                    .andExpect(status().isOk)
                    .andReturn()
                    .response
                    .contentAsString,
                Array<Post>::class.java
            )

        assertEquals(post.size, 1)
    }

    @Test
    @Order(5)
    fun getPost() {
        val testTitle = "testTitleInfo"
        val testContent = "testContentInfo"

        val requestParams = JSONObject()
        requestParams["title"] = testTitle
        requestParams["content"] = testContent

        val passport = passportTestRepository.generatePassport()

        val post =
            mapper.readValue(
                mvc
                    .perform(
                        post("/api/v1/post")
                            .header("Content-Type", "application/json")
                            .header("x-moom-passport-user", passport!!.passport)
                            .header("x-moom-passport-key", passport!!.key)
                            .content(requestParams.toJSONString())
                    ).andExpect(status().isOk)
                    .andReturn()
                    .response
                    .contentAsString,
                Post::class.java
            )

        assertEquals(post.title, testTitle)
        assertEquals(post.content, testContent)

        val postId = post.id

        val resultPost =
            mapper.readValue(
                mvc
                    .perform(
                        get("/api/v1/post/$postId")
                            .header("Content-Type", "application/json")
                    ).andExpect(status().isOk)
                    .andReturn()
                    .response
                    .contentAsString,
                Post::class.java
            )

        assertEquals(resultPost.id, postId)
        assertEquals(resultPost.title, testTitle)
        assertEquals(resultPost.content, testContent)
    }

    @Test
    @Order(6)
    fun writePostEmptyTitle() {
        val testContent = "testContent"

        val requestParams = JSONObject()
        requestParams["title"] = ""
        requestParams["content"] = testContent

        val passport = passportTestRepository.generatePassport()

        mvc
            .perform(
                post("/api/v1/post")
                    .header("Content-Type", "application/json")
                    .header("x-moom-passport-user", passport!!.passport)
                    .header("x-moom-passport-key", passport!!.key)
                    .content(requestParams.toJSONString())
            ).andExpect(status().isBadRequest)
            .andReturn()
            .response
            .contentAsString
    }

    @Test
    @Order(7)
    fun writePostEmptyContent() {
        val testTitle = "testTitle"

        val requestParams = JSONObject()
        requestParams["title"] = testTitle
        requestParams["content"] = ""

        val passport = passportTestRepository.generatePassport()

        mvc
            .perform(
                post("/api/v1/post")
                    .header("Content-Type", "application/json")
                    .header("x-moom-passport-user", passport!!.passport)
                    .header("x-moom-passport-key", passport!!.key)
                    .content(requestParams.toJSONString())
            ).andExpect(status().isBadRequest)
            .andReturn()
            .response
            .contentAsString
    }
}
