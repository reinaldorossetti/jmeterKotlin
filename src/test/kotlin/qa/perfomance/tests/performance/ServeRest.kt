package qa.perfomance.tests.performance

import com.github.javafaker.Faker
import net.fourward.base.BaseTestPerformance
import org.apache.commons.io.FileUtils
import org.apache.jmeter.threads.JMeterVariables
import org.assertj.core.api.Assertions
import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.http.MimeTypes
import org.junit.jupiter.api.Test
import us.abstracta.jmeter.javadsl.JmeterDsl.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*


class PerformanceServeRest: BaseTestPerformance() {

    private var configJmeter = setUp()
    // usando o Faker para gerar dados aleatorios.
    var faker = Faker()

    /**
     * 3 threadGroup + threadCount=560 + iterations=3 ->> Igual a 5.040 requests.
     * Em 10 Maquinas 50.400 requests.
     */


    @Test
    //@Disabled
    @Throws(IOException::class)
    fun testPerformance() {
        // create object of Path
        val sdf = SimpleDateFormat("ddMMyyyy")
        val currentDate = sdf.format(Date())
        val name = "target//reportJmeter//$currentDate"
        FileUtils.deleteDirectory(File(pathProject + name))

        val stats = testPlan(
            threadGroup("Group - cadastro de usuarios").children(
                httpSampler("https://serverest.dev/usuarios/")
                    .method(HttpMethod.POST)
                    .post("{ \"nome\": \"\${NOME}\", \"email\": \"\${EMAIL}\", \"password\": \"\${PASS}\", \"administrador\": \"true\" }",
                        MimeTypes.Type.TEXT_PLAIN)
                    .children(
                        jsr223PreProcessor { s -> s.vars.put("EMAIL", buildRequestBody(s.vars)) }
                    )
                    .contentType(MimeTypes.Type.APPLICATION_JSON)
                    .header("Accept", "application/json")
                    .header("monitor", "false"),
                uniformRandomTimer(1000, 3000),
                //resultsTreeVisualizer()
            ).rampTo(configJmeter.threadCount, Duration.ofSeconds(5)).holdIterating(configJmeter.iterations),
            threadGroup("Group - usuarios").children(
                httpSampler("https://serverest.dev/usuarios/")
                .method(HttpMethod.GET)
                .contentType(MimeTypes.Type.APPLICATION_JSON)
                .header("Accept", "application/json")
                .header("monitor", "false"),
                uniformRandomTimer(1000, 3000),
                responseAssertion().containsSubstrings("quantidade"),
                //resultsTreeVisualizer()
            ).rampTo(configJmeter.threadCount, Duration.ofSeconds(5)).holdIterating(configJmeter.iterations),
            threadGroup("Group - produtos").children(
                httpSampler("https://serverest.dev/produtos/")
                .method(HttpMethod.GET)
                .contentType(MimeTypes.Type.APPLICATION_JSON)
                .header("Accept", "application/json")
                .header("monitor", "false"),
                uniformRandomTimer(1000, 3000),
                responseAssertion().containsSubstrings("quantidade"),
                //resultsTreeVisualizer()
        ).rampTo(configJmeter.threadCount, Duration.ofSeconds(configJmeter.timeoutIteration)).holdIterating(configJmeter.iterations),
        htmlReporter(name),
        ).run()
        Assertions.assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(30))
    }

    fun buildRequestBody(vars: JMeterVariables): String {
        val varEmail = "EMAIL"
        val varNome = "NOME"
        val varPass = "PASS"
        vars.putObject(varEmail, faker.internet().emailAddress())
        vars.putObject(varNome, faker.name().fullName())
        vars.putObject(varPass, faker.internet().password())
        return faker.internet().emailAddress()
    }

}