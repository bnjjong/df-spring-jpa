package io.df.dfspringjpa.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "question_groups")
class QuestionGroup(
    @Column(nullable = false)
    var name: String
) {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID = UUID.randomUUID()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false, columnDefinition = "BINARY(16)")
    lateinit var survey: Survey

    @OneToMany(
        mappedBy = "group",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val questions: MutableList<Question> = mutableListOf()

    fun addQuestion(question: Question) {
        question.group = this
        questions.add(question)
    }
}