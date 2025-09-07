package io.df.dfspringjpa.exception

import io.df.dfspringjpa.exception.adoc.ADocAdapter
import io.df.dfspringjpa.exception.adoc.ALibInMemoryClient
import io.df.dfspringjpa.exception.bdoc.BDocAdapter
import io.df.dfspringjpa.exception.bdoc.BLibMemClient

class MainRunner {
}

// ---- 짧은 사용 예 ----
fun main() {
    val aAdapter: DocumentPort = ADocAdapter(ALibInMemoryClient())
    val bAdapter: DocumentPort = BDocAdapter(BLibMemClient())

    aAdapter.create("A-1", "Hello A".toByteArray())
    println(String(aAdapter.load("A-1"))) // Hello A

    bAdapter.create("B-1", "Hello B".toByteArray())
    println(String(bAdapter.load("B-1"))) // Hello B

    // 이미 존재하는 문서를 다시 만들면 도메인 예외로 변환됨
    try {
        aAdapter.create("A-1", "dup".toByteArray())
    } catch (e: StorageException.AlreadyExists) { // 결국엔 여기서 도메인 예외로 처리 함.
        println("도메인 예외로 변환 OK: ${e.message}")
    }
}