package net.fourward.base

import com.google.gson.Gson
import org.junit.jupiter.api.TestInstance
import qa.perfomance.dados.ConfigJmeter
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTestPerformance {

    val pathProject : String = System.getProperty("user.dir")
    private var browserConfigFile = File("$pathProject/src/test/kotlin/resources/body/config.json").readText(Charsets.UTF_8)

    private fun configJmeter(): ConfigJmeter {
        return Gson().fromJson(browserConfigFile, ConfigJmeter::class.java)
    }

    fun setUp(): ConfigJmeter {
            val threadCount: String? = System.getProperty("threadCount")
            val iterations: String? = System.getProperty("iterations")
            val iterationsTimeout: String? = System.getProperty("timeoutIterations")
            println("threadCount: $threadCount, iterations: $iterations")
            val newConfig = configJmeter()
            if (!threadCount.isNullOrBlank()) {
                newConfig.threadCount = threadCount.toInt()
            }
            if (!iterations.isNullOrBlank()) {
                newConfig.iterations = iterations.toInt()
            }
            if (!iterationsTimeout.isNullOrBlank()) {
                newConfig.timeoutIteration = iterationsTimeout.toLong()
            }
            val newValues = Gson().toJson(newConfig)
            return Gson().fromJson(newValues, ConfigJmeter::class.java)
        }
}