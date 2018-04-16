set MAVEN_OPTS=-Xms512m ^
-Xmx1024m ^
-XX:MaxPermSize=256m ^
-Xdebug ^
-Xnoagent ^
-Djava.compiler=NONE ^
-Xrunjdwp:transport=dt_socket,address=8090,server=y,suspend=n ^
-Dhttp.proxyHost=proxy ^
-Dhttp.proxyPort=10080 ^
-Dhttps.proxyHost=proxy ^
-Dhttps.proxyPort=10080 ^
-Denvironment=accept ^
-Denvironment.active=true ^
-Duser.language= ^
-Duser.country= ^
-Dlogfiles.location=c:/cse/logfiles ^
-Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false

mvn spring-boot:run -Dspring-boot.run.profiles=dev

REM -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8001"