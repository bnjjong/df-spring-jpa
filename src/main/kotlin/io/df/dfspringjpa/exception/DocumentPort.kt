package io.df.dfspringjpa.exception

interface DocumentPort {
    fun load(id: String): ByteArray        // throws StorageException (런타임)
    fun create(id: String, bytes: ByteArray)
}
