package io.df.dfspringjpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.df.dfspringjpa", "io.df.df.springjpa"])
class DfSpringJpaApplication

fun main(args: Array<String>) {
    runApplication<DfSpringJpaApplication>(*args)
}
