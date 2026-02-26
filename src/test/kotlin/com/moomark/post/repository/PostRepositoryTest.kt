package com.moomark.post.repository

import com.moomark.post.model.entity.Post
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {
    @Autowired
    private lateinit var postRepository: PostRepository

    @BeforeEach
    fun initTest() {
        postRepository.deleteAll()
    }

    @Test
    fun findByTitle() {
        // given
        val firstSavedData =
            Post
                .builder()
                .title("FIRST TITLE")
                .content("FIRST CONTENT")
                .userId("FIRST USER ID")
                .build()

        val secondSavedData =
            Post
                .builder()
                .title("SECOND TITLE")
                .content("SECOND CONTENT")
                .userId("SECOND USER ID")
                .build()

        // when
        postRepository.save(firstSavedData)
        postRepository.save(secondSavedData)

        // then
        val savedList = postRepository.findByTitleContaining("TITLE")
        Assertions.assertThat(savedList).hasSize(2)
    }

    @Test
    fun findByUserId() {
        // given
        val firstSavedData =
            Post
                .builder()
                .title("FIRST TITLE")
                .content("FIRST CONTENT")
                .userId("FIRST USER ID")
                .build()

        val secondSavedData =
            Post
                .builder()
                .title("SECOND TITLE")
                .content("SECOND CONTENT")
                .userId("SECOND USER ID")
                .build()

        val thirdSavedData =
            Post
                .builder()
                .title("THIRD TITLE")
                .content("THIRD CONTENT")
                .userId("FIRST USER ID")
                .build()

        // when
        postRepository.save(firstSavedData)
        postRepository.save(secondSavedData)
        postRepository.save(thirdSavedData)

        // then
        val savedList = postRepository.findByUserId("FIRST USER ID")
        Assertions.assertThat(savedList).hasSize(2)
    }
}
