package io.df.dfspringjpa.survey.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "surveys")
class Survey internal constructor(
    // 필수 생성자 프로퍼티
    title: String,

    @Id
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null,
) {
    init {
        initId()
    }

    private fun initId() {
        this.id = this.id ?: UUID.randomUUID()
    }

    @Column(nullable = false)
    var title: String = title
        protected set

    // ================== child ==================
    @OneToMany(
        mappedBy = "survey",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val groups: MutableList<QuestionGroup> = mutableListOf()

    internal fun addGroup(group: QuestionGroup) {
        group.applySurvey(this)
        groups.add(group)
//        (groups ?: mutableListOf<QuestionGroup>().also { groups = it }).add(group)
    }
    // ================== child ==================

//    @PrePersist
//    @PreUpdate
//    private fun validateBeforePersist() {
//        if (id == null) {
//            throw IllegalStateException("Survey.id must not be null before persist/update")
//        }
//        if (groups == null || groups!!.isEmpty()) {
//            throw IllegalStateException("Survey.groups must not be empty before persist/update")
//        }
//    }
}

