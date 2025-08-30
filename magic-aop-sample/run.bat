setlocal enabledelayedexpansion

rem === JARファイルが格納されているフォルダ ===
set JAR_DIR=C:\projects\github\hondams\magic-aop\magic-aop\target\libs

rem === クラスパスを初期化 ===
set CP=

rem === JARファイルを列挙してクラスパスを構築 ===
for %%f in (%JAR_DIR%\*.jar) do (
    if "!CP!"=="" (
        set CP=%%f
    ) else (
        set CP=!CP!;%%f
    )
)

java -javaagent:C:\projects\github\hondams\magic-aop\magic-aop\target\magic-aop-0.0.1-SNAPSHOT.jar -cp "!CP!" -jar target\magic-aop-sample-0.0.1-SNAPSHOT.jar
pause
