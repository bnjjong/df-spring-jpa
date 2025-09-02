package io.df.dfspringjpa.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "questions")
class Question internal  constructor(
    // 필수 생성자 프로퍼티
    text: String,
    type: String,

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
    var text: String = text
        protected set

    @Column(nullable = false)
    var type: String = type
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, columnDefinition = "BINARY(16)")
    lateinit var group: QuestionGroup

    companion object {
        fun create(text: String, type: String): Question = Question(text = text, type = type, id = null)
    }
}