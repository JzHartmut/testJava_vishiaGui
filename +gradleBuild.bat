if "%JAVA_HOME%" == "" set JAVA_HOME=c:\Programs\Java\jdk1.8.0_211
set JAVAC_HOME=%JAVA_HOME%
call +mkLinkBuildGradle.bat
:repeat
cls
call gradle.bat --warning-mode all srcZip 2>build\err.txt
type build\err.txt
echo close window or repeat
pause
goto :repeat
call inetBrowser %CD%\build\reports\tests\test\index.html

