setlocal enabledelayedexpansion

rem === JAR�t�@�C�����i�[����Ă���t�H���_ ===
set JAR_DIR=C:\projects\github\hondams\magic-aop\magic-aop\target\libs

rem === �N���X�p�X�������� ===
set CP=

rem === JAR�t�@�C����񋓂��ăN���X�p�X���\�z ===
for %%f in (%JAR_DIR%\*.jar) do (
    if "!CP!"=="" (
        set CP=%%f
    ) else (
        set CP=!CP!;%%f
    )
)

java -javaagent:C:\projects\github\hondams\magic-aop\magic-aop\target\magic-aop-0.0.1-SNAPSHOT.jar -cp "!CP!" -jar target\magic-aop-sample-0.0.1-SNAPSHOT.jar
pause
