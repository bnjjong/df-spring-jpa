package io.df.dfspringjpa.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "surveys")
class Survey(
    @Column(nullable = false)
    var title: String
) {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID = UUID.randomUUID()

    @OneToMany(
        mappedBy = "survey",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val groups: MutableList<QuestionGroup> = mutableListOf()

    fun addGroup(group: QuestionGroup) {
        group.survey = this
        groups.add(group)
    }
}

