package com.moomark.post.model.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
@EntityListeners(AuditingEntityListener::class)
class Category(
    @Column(name = "category_type")
    var categoryType: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(mappedBy = "category")
    var post: MutableList<PostCategory> = mutableListOf()

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var parent: Category? = null

    @OneToMany(mappedBy = "parent")
    var childList: MutableList<Category> = mutableListOf()

    fun updateCategoryInfo(categoryType: String) {
        this.categoryType = categoryType
    }

    fun addChildCategory(childCategory: Category) {
        childCategory.setParents(this)
        this.childList.add(childCategory)
    }

    fun removeChildCategory(category: Category) {
        this.childList.remove(category)
    }

    fun setParents(parentCategory: Category) {
        this.parent = parentCategory
    }

    fun getParentAfterNullCheck(): Long = parent?.id ?: 0L
}
