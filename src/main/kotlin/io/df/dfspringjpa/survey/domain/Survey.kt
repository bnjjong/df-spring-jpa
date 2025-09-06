package io.df.dfspringjpa.survey.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
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

    @OneToMany(
        mappedBy = "survey",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val groups: MutableList<QuestionGroup> = mutableListOf()

    internal fun addGroup(group: QuestionGroup) {
        group.survey = this
        groups.add(group)
    }

    companion object {
        fun create(title: String): Survey = Survey(title = title, id = null)
    }
}

