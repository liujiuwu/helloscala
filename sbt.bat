set SCRIPT_DIR=%~dp0
java -Xmx768M -Xss1M -XX:MaxPermSize=256M -XX:+CMSClassUnloadingEnabled -jar "%SCRIPT_DIR%\project\sbt-launch-0.13.0.jar" %*
