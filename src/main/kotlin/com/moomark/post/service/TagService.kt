package com.moomark.post.service

import com.moomark.post.model.dto.TagDto
import com.moomark.post.model.entity.Tag
import com.moomark.post.repository.TagRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TagService(
    private val tagRepository: TagRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun addTag(information: String): Long {
        val tag =
            Tag(
                information = information,
            )

        log.info("Add information : {}", information)
        return tagRepository.save(tag).id!!
    }

    fun deleteTag(id: Long) {
        tagRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun findTagById(id: Long): TagDto {
        val tag = tagRepository.findById(id).orElseThrow()

        return TagDto(
            id = tag?.id,
            information = tag?.information,
        )
    }
}
