# jmeterKotlin
Teste usando o Jmeter DSL em Kotlin

Linha de comando:
````bash
mvn clean test -Dtest=PerformanceServeRestUsuarios -DthreadCount=50 -Diterations=1
mvn clean test -Dtest=PerformanceServeRestUsuarios -DthreadCount=100 -Diterations=1
mvn clean test -Dtest=PerformanceServeRestUsuarios -DthreadCount=200 -Diterations=1
mvn clean test -Dtest=PerformanceServeRestUsuarios -DthreadCount=300 -Diterations=1
mvn clean test -Dtest=PerformanceServeRestUsuarios -DthreadCount=400 -Diterations=1
mvn clean test -Dtest=PerformanceServeRestUsuarios -DthreadCount=600 -Diterations=1
mvn clean test -Dtest=PerformanceServeRestUsuarios -DthreadCount=900 -Diterations=1
mvn clean test -Dtest=PerformanceServeRestUsuarios -DthreadCount=1000 -Diterations=1
````

O comando já esta na pipeline, vc pode ir mudando os valores e as iterações, ou aumentar o número de maquinas.
https://github.com/reinaldorossetti/jmeterKotlin/blob/main/.github/workflows/main.yml
