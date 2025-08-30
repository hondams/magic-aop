set EXECUTION_JAR=target\magic-aop-sample-0.0.1-SNAPSHOT.jar

set JAVA_AGENT_JAR=%~dp0..\magic-aop\target\magic-aop-0.0.1-SNAPSHOT.jar
set MAGIC_AOP_LIB_DIR=%~dp0..\magic-aop\target\libs
set MAGIC_AOP_CONFIG=magic-aop.config

for %%I in ("%EXECUTION_JAR%") do set EXECUTION_JAR=%%~fI
for %%I in ("%JAVA_AGENT_JAR%") do set JAVA_AGENT_JAR=%%~fI
for %%I in ("%MAGIC_AOP_LIB_DIR%") do set MAGIC_AOP_LIB_DIR=%%~fI
for %%I in ("%MAGIC_AOP_CONFIG%") do set MAGIC_AOP_CONFIG=%%~fI

set JAVA_TOOL_OPTIONS=-javaagent:%JAVA_AGENT_JAR%=libdir=%MAGIC_AOP_LIB_DIR%;config=%MAGIC_AOP_CONFIG%;loglevel=DEBUG
java -jar %EXECUTION_JAR%
pause
