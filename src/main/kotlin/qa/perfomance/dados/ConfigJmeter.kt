package qa.perfomance.dados

data class ConfigJmeter(
    var threadCount: Int= 100,
    var iterations: Int=10,
    var timeoutIteration: Long =10
)

