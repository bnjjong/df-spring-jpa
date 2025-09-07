package io.df.dfspringjpa.exception.bdoc

import io.df.dfspringjpa.exception.DocumentPort
import io.df.dfspringjpa.exception.StorageException

class BDocAdapter(private val b: BLibClient) : DocumentPort {
    override fun load(id: String): ByteArray = try {
        b.fetch("/docs/$id")
    } catch (t: Throwable) {
        throw StorageException.IoFailure("조회", id, t)
    }

    override fun create(id: String, bytes: ByteArray) {
        try {
            b.put("/docs/$id", bytes)
        } catch (t: Throwable) {
            if (t is BLibConflict) throw StorageException.AlreadyExists(id, t)
            else throw StorageException.IoFailure("생성", id, t)
        }
    }
}
