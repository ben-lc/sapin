package com.github.ben_lc.sapin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class SapinApplication

fun main(args: Array<String>) {
  runApplication<SapinApplication>(*args)
}
