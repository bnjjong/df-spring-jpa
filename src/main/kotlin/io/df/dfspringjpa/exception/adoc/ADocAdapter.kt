package io.df.dfspringjpa.exception.adoc

import io.df.dfspringjpa.exception.DocumentPort
import io.df.dfspringjpa.exception.StorageException

class ADocAdapter(private val a: ALibClient) : DocumentPort {
    override fun load(id: String): ByteArray = try {
        a.read("/docs/$id")
    } catch (t: Throwable) {
        throw StorageException.IoFailure("조회", id, t) // 경계에서 감싸기
    }

    override fun create(id: String, bytes: ByteArray) {
        try {
            a.write("/docs/$id", bytes)
        } catch (t: Throwable) {
            when (t) {
                is ALibAlreadyExists -> throw StorageException.AlreadyExists(id, t)
                else -> throw StorageException.IoFailure("생성", id, t)
            }
        }
    }
}
