package io.df.dfspringjpa.survey.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "question_groups")
class QuestionGroup internal  constructor(
    // 필수 생성자 프로퍼티
    name: String,

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
    var name: String = name
        protected set

    // ================== parent ==================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "survey_id",
        nullable = false,
        columnDefinition = "BINARY(16)")
    lateinit var survey: Survey
        protected set

    internal fun applySurvey(survey: Survey) {
        this.survey = survey
    }
    // ================== parent ==================

    // ================== child ==================
    @OneToMany(
        mappedBy = "group",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val questions: MutableList<Question> = mutableListOf()

    // support function
    internal fun addQuestion(question: Question) {
        question.applyGroup(this)
        questions.add(question)
    }
    // ================== child ==================

    @PrePersist
    @PreUpdate
    private fun validateBeforePersist() {
        if (id == null) {
            throw IllegalStateException("QuestionGroup.id must not be null before persist/update")
        }
        // Not required by the issue, but you may also enforce non-empty questions if desired
    }
}