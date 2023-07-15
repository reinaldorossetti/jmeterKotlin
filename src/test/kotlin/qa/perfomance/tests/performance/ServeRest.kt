package qa.perfomance.tests.performance

import com.github.javafaker.Faker
import net.fourward.base.BaseTestPerformance
import org.apache.commons.io.FileUtils
import org.apache.http.entity.ContentType
import org.apache.jmeter.protocol.http.util.HTTPConstants
import org.apache.jmeter.threads.JMeterVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import us.abstracta.jmeter.javadsl.JmeterDsl.*
import us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer.dashboardVisualizer
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*


class PerformanceServeRestUsuarios: BaseTestPerformance() {

    private var configJmeter = setUp()
    // usando o Faker para gerar dados aleatorios.
    var faker = Faker()
    var URL = "http://192.168.18.4:3000/"

    /**
     * 3 threadGroup + threadCount=560 + iterations=3 ->> Igual a 5.040 requests.
     * Em 10 Maquinas 50.400 requests.
     */


    @Test
    //@Disabled
    @Throws(IOException::class)
    fun testPerformanceThreadGroup() {
        // create object of Path
        val currentDate = SimpleDateFormat("ddMMyyyy").format(Date())
        val name = "target//reportJmeter//$currentDate"
        //FileUtils.deleteDirectory(File(pathProject + name))

        val stats = testPlan(
            // X (configJmeter.threadCount) threads for Y (configJmeter.iterations) iterations each
            // Sample: 50 threads\users virtuais (usuários simultâneos) que enviam 50 solicitações HTTP.
            threadGroup(configJmeter.threadCount, configJmeter.iterations).children(
                httpSampler("$URL/usuarios/")
                    .method(HTTPConstants.POST)
                    .post("{ \"nome\": \"\${NOME}\", \"email\": \"\${EMAIL}\", \"password\": \"\${PASS}\", \"administrador\": \"true\" }",
                        ContentType.TEXT_PLAIN)
                    .children(
                        jsr223PreProcessor { s -> s.vars.put("EMAIL", buildRequestBody(s.vars)) }
                    )
                    .contentType(ContentType.APPLICATION_JSON)
                    .header("Accept", "application/json"),
                    .header("monitor", "false"),
                // Temporizador Uniforme Aleatório que pausa a thread com um tempo aleatório com distribuição uniforme.
                // valor minimo e valor maximo em milissegundo
                uniformRandomTimer(500, 5000),
            ),
            threadGroup(configJmeter.threadCount, configJmeter.iterations).children(
                httpSampler("$URL/usuarios/")
                .method(HTTPConstants.GET)
                .contentType(ContentType.APPLICATION_JSON)
                .header("Accept", "application/json"),
                //.header("monitor", "false"),
                uniformRandomTimer(500, 5000),
                responseAssertion().containsSubstrings("quantidade"),
        ),
        // pega o log de cada request
        jtlWriter("//target//reportJmeter//logs_" + Instant.now().toString().replace(":", "-") + ".jtl"),

        // pega o retorno da requisicao em arquivos, vai pegar de cada requisição
        // responseFileSaver(System.getProperty("user.dir") + "\\logs\\" + Instant.now().toString().replace(":", "-") + "-response"),

        // visualizando em grafico de arvore.
        // resultsTreeVisualizer(),
        // dashboard em tempo real.
        // dashboardVisualizer(),
        htmlReporter(name),
        ).run()
        // quantidade de erros igual a zero.
        assertThat(stats.overall().errorsCount()).isEqualTo(0)
        // o tempo maximo de ser de 10 segundos.
        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(10))
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
