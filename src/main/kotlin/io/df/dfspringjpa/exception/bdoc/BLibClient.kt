package io.df.dfspringjpa.exception.bdoc

// === [BLib] 라이브러리 B: 퍼블릭 API 예시 ===============================

/**
 * 라이브러리 B가 노출하는 전용 예외들
 */
open class BLibException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/** 자원 충돌(이미 존재, 버전 충돌, 락 충돌 등 폭넓게 사용) */
class BLibConflict(message: String) : BLibException(message)

/** 스토리지 내부 에러(권한, 연결, 손상 등) */
class BLibStorageError(message: String, cause: Throwable? = null) : BLibException(message, cause)

/**
 * 라이브러리 B 클라이언트(퍼블릭 인터페이스)
 * - fetch: 없거나 접근 불가 시 BLibStorageError
 * - put: 이미 있으면 BLibConflict
 */
interface BLibClient {
    fun fetch(path: String): ByteArray
    fun put(path: String, data: ByteArray)
}

/**
 * 간단한 In-Memory 구현체 (샘플/테스트 용도)
 */
class BLibMemClient : BLibClient {

    private val store = java.util.concurrent.ConcurrentHashMap<String, ByteArray>()

    override fun fetch(path: String): ByteArray {
        return store[path] ?: throw BLibStorageError("resource not found: $path")
    }

    override fun put(path: String, data: ByteArray) {
        if (store.containsKey(path)) {
            throw BLibConflict("conflict: already exists -> $path")
        }
        store[path] = data
    }

    // 테스트 편의용
    fun exists(path: String): Boolean = store.containsKey(path)
}
