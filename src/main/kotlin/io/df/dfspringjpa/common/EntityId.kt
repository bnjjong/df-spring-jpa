package io.df.dfspringjpa.common

import java.io.Serializable

interface EntityId<T> : Serializable {
    /**
     * 엔티티의 고유 식별자입니다.
     */
    val id : T

    /**
     * ID의 문자열 표현을 반환합니다.
     * @return ID의 문자열 표현
     */
    override fun toString(): String
}