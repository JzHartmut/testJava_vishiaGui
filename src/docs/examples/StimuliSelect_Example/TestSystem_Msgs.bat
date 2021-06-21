echo off
set SOADDR_OWN=127.0.0.1:45041
set SOADDR_GUI=127.0.0.1:45040
REM uncomment to see usage:
libs\socketcmd.exe  
:loop
echo first wait for the start message via %SOADDR_OWN% from Stimuli Selection
echo ... expected: test abort or finish from socket 
::echo on
:ask
libs\socketcmd.exe -own:%SOADDR_OWN% -dst:%SOADDR_GUI% -to:2000 -cmd step -rx test abort finish
::pause
::echo %errorlevel%
if errorlevel 255 goto :loop
if errorlevel 254 goto :GuinotReady
if errorlevel 4 goto :unknown
if errorlevel 3 goto :finish
if errorlevel 2 goto :abort
if errorlevel 1 goto :test
:unknown
echo faulty cmd received %errorlevel%, wait 2 sec and go in loop
libs\socketcmd.exe -own:%SOADDR_OWN% -dst:%SOADDR_GUI% -to:2000
goto :ask
:GuinotReady
echo the partner is not ready, socket error 10054, go in loop
goto :ask
:test
echo ....test is running 3 seconds
type genScripts\testfile_text.txt
libs\socketcmd.exe -to:3000
REM if test is finished, send step to StimuliSelector.
REM it is possible because the StimuliSelector has a message queue
REM and it may be faster as a file as semaphore.
echo test finished, sends "step" via socket:
goto :loop
:abort
echo test was aborted
pause
goto :loop
exit /b
:finish
echo test series finished
::pause
goto :loop
exit /b

