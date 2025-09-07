package io.df.dfspringjpa.exception

sealed class StorageException(message: String, cause: Throwable? = null)
    : RuntimeException(message, cause) {
    class AlreadyExists(val id: String, cause: Throwable? = null)
        : StorageException("문서가 이미 존재합니다: $id", cause)
    class IoFailure(val action: String, val id: String, cause: Throwable? = null)
        : StorageException("문서 $action 중 I/O 오류: $id", cause)
}