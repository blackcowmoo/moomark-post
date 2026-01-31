package com.moomark.post.model.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
@EntityListeners(AuditingEntityListener::class)
class Tag(
    @Column(name = "tag_information")
    var information: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(mappedBy = "tag")
    var post: MutableList<PostTag> = mutableListOf()
}
