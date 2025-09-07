package io.df.dfspringjpa.exception.adoc

// === [ALib] 라이브러리 A: 퍼블릭 API 예시 ===============================

/**
 * 라이브러리 A가 노출하는 전용 예외들 (라이브러리 내부 네이밍/구조를 가정)
 */
open class ALibException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/** 같은 경로에 파일이 이미 있을 때 */
class ALibAlreadyExists(message: String) : ALibException(message)

/** 파일 시스템 또는 네트워크 I/O 문제 */
class ALibIoException(message: String, cause: Throwable? = null) : ALibException(message, cause)

/**
 * 라이브러리 A 클라이언트(퍼블릭 인터페이스)
 * - read: 존재하지 않으면 ALibIoException("not found") 등 I/O 류 예외 발생
 * - write: allowOverwrite=false 이고 이미 있으면 ALibAlreadyExists 발생
 */
interface ALibClient {
    fun read(path: String): ByteArray
    fun write(path: String, data: ByteArray)
}

/**
 * 간단한 In-Memory 구현체 (샘플/테스트 용도)
 * - 실제 라이브러리에서는 원격 스토리지나 파일시스템 호출이 들어간다고 가정
 */
class ALibInMemoryClient(
    private val allowOverwrite: Boolean = false
) : ALibClient {

    private val store = java.util.concurrent.ConcurrentHashMap<String, ByteArray>()

    override fun read(path: String): ByteArray {
        return store[path] ?: throw ALibIoException("not found: $path")
    }

    override fun write(path: String, data: ByteArray) {
        if (!allowOverwrite && store.containsKey(path)) {
            throw ALibAlreadyExists("already exists: $path")
        }
        store[path] = data
    }

    // 테스트 편의용
    fun exists(path: String): Boolean = store.containsKey(path)
}
