@ECHO OFF
setlocal
set WRAPPER_DIR=%~dp0\.mvn\wrapper
set WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
set PROPS_FILE=%WRAPPER_DIR%\maven-wrapper.properties
IF NOT EXIST "%WRAPPER_JAR%" (
  echo Downloading Maven Wrapper jar...
  for /f "tokens=2 delims==" %%A in ('findstr /R "^wrapperUrl=" "%PROPS_FILE%"') do set URL=%%A
  if exist "%SystemRoot%\System32\curl.exe" (
    curl -fsSL -o "%WRAPPER_JAR%" %URL%
  ) else (
    powershell -Command "(New-Object Net.WebClient).DownloadFile('%URL%', '%WRAPPER_JAR%')"
  )
)
set JAVA_EXE=%JAVA_HOME%in\java.exe
if not exist "%JAVA_EXE%" set JAVA_EXE=java
"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
