package qa.perfomance

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JmeterKotlinApplication

fun main(args: Array<String>) {
	runApplication<JmeterKotlinApplication>(*args)
}
