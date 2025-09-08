package io.df.dfspringjpa.exception2

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

// DAO 인터페이스: Repo가 기대하는 최소 메서드
interface Dao {
    /** id로 캠페인을 조회. 없으면 NotFoundException */
    @Throws(NotFoundException::class)
    fun selectCampaign(id: String): Campaign

    /** remain 값을 갱신. 대상이 없으면 NotFoundException */
    @Throws(NotFoundException::class)
    fun updateRemain(id: String, newRemain: Long)

    /** 새 캠페인 생성. 이미 존재하면 IllegalStateException 등 던질 수 있음 */
    fun insertCampaign(c: Campaign)
}


@Repository
@org.springframework.context.annotation.Primary
class JdbcDao(
    private val jdbc: NamedParameterJdbcTemplate
) : Dao {

    private val campaignMapper = RowMapper<Campaign> { rs, _ ->
        Campaign(
            id = rs.getString("id"),
            remain = rs.getLong("remain"),
            dailyQuota = rs.getLong("daily_quota")
        )
    }

    override fun selectCampaign(id: String): Campaign {
        val sql = """
            SELECT id, remain, daily_quota
              FROM campaigns
             WHERE id = :id
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("id", id)

        return try {
            jdbc.queryForObject(sql, params, campaignMapper)
                ?: throw NotFoundException("campaign not found: $id")
        } catch (e: EmptyResultDataAccessException) {
            throw NotFoundException("campaign not found: $id")
        }
    }

    override fun updateRemain(id: String, newRemain: Long) {
        val sql = """
            UPDATE campaigns
               SET remain = :remain
             WHERE id = :id
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("id", id)
            .addValue("remain", newRemain)

        val updated = jdbc.update(sql, params)
        if (updated == 0) {
            // 대상이 없거나, (옵션) 낙관적 잠금 버전 불일치 등
            throw NotFoundException("campaign not found for update: $id")
        }
    }

    override fun insertCampaign(c: Campaign) {
        val sql = """
            INSERT INTO campaigns (id, remain, daily_quota)
            VALUES (:id, :remain, :daily_quota)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", c.id)
            .addValue("remain", c.remain)
            .addValue("daily_quota", c.dailyQuota)
        jdbc.update(sql, params)
    }
}

@Repository
class JpaDao(
    private val jpa: CampaignJpaRepository
) : Dao {
    override fun selectCampaign(id: String): Campaign {
        val entity = jpa.findById(id).orElseThrow { NotFoundException("campaign not found: $id") }
        return Campaign(
            id = entity.id,
            remain = entity.remain,
            dailyQuota = entity.dailyQuota
        )
    }

    override fun updateRemain(id: String, newRemain: Long) {
        val entity = jpa.findById(id).orElseThrow { NotFoundException("campaign not found for update: $id") }
        entity.remain = newRemain
        jpa.save(entity)
    }

    override fun insertCampaign(c: Campaign) {
        if (jpa.existsById(c.id)) {
            throw IllegalStateException("campaign already exists: ${'$'}{c.id}")
        }
        jpa.save(CampaignEntity(id = c.id, remain = c.remain, dailyQuota = c.dailyQuota))
    }
}
