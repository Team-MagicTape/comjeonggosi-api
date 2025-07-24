package com.comjeonggosi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ComjeonggosiApplication

fun main(args: Array<String>) {
    runApplication<ComjeonggosiApplication>(*args)
}
