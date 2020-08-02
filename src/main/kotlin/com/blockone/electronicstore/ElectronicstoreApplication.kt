package com.blockone.electronicstore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ElectronicstoreApplication

fun main(args: Array<String>) {
    System.setProperty("spring.config.name", "application")
    runApplication<ElectronicstoreApplication>(*args)
}
