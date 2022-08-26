@echo off
REM invoke from current dir, where this file is stored.
cd /D %~dp0
set ARGS=%1
if "" == "%ARGS%" set ARGS=./Fcmd.cfg/Fcmd.args
echo %ARGS%
set LOGDIR=%TMP%\Fcmd
if not exist %LOGDIR% mkdir %LOGDIR%
if exist %LOGDIR%\err1.txt del %LOGDIR%\err1.txt

REM Decision whether a 32.bit or 64.bit SWT library should be used depends on the java version 32 or 64, not on the Operation System.
set SWTJAR=org.eclipse.swt.win32.win32.x86_64.jar

REM if javaw is used, the console window remain open but can be closed manually independent of the java run.
REM The >out and >err can be used. If start is used here, >out and >err do not work. 
REM Then no information is given on faulty conditions, especially missing jars.
REM Therefore 'start' cannot be used here.
REM write out the command line to help explore the starting conditions on faulty situation:
REM to evaluate error conditions, change javaw to java!
@echo on  
javaw -cp ../../../tools/vishiaBase.jar;../../../tools/vishiaGui.jar;../../../tools/%SWTJAR% org.vishia.commander.Fcmd --@%ARGS%
@echo off
::type %LOGDIR%\err1.txt
if errorlevel 1 pause
REM exit /b means, the console window remain open though this called batch will be finished. exit pure closes the console.
exit /b

