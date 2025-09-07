package io.df.dfspringjpa.domain

import io.df.dfspringjpa.survey.domain.Survey
import io.df.dfspringjpa.survey.repository.SurveyRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class SurveyIntegrationTest @Autowired constructor(
    private val repository: SurveyRepository,
    private val em: TestEntityManager,
) : BehaviorSpec({

    // enable Spring context support for Kotest
    extensions(SpringExtension)

    given("Spring Data JPA context") {
        then("SurveyRepository should be injected") {
            repository.shouldNotBeNull()
        }
    }

    given("a Survey without any group") {
        `when`("trying to save it") {
            then("it should fail with an exception due to entity validation") {
                val invalid = Survey(title = "Invalid Survey")
                val result = kotlin.runCatching { em.persist(invalid); em.flush() }
                result.isFailure shouldBe true
            }
        }
    }
})
