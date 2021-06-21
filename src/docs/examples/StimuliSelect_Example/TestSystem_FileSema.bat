echo off
REM uncomment to see usage:
::libs\socketcmd.exe
:loop
echo first wait for presence of the semaphore file from Stimuli Selection
echo ... expected: genScripts\test.msg, ...\abort.msg or ...\finish.msg
::not proven: 
::libs\socketcmd.exe -own:127.0.0.1:0xaff1 -dst:127.0.0.1:0xaff0 -cmd wait -rx test abort finish
::if errorlevel 1 goto :test
::if errorlevel 2 goto :abort
::if errorlevel 3 goto :finish

::pause
::goto :finish
:waitstart
if exist genScripts\test.msg goto :test
if exist genScripts\abort.msg goto :abort
if exist genScripts\finish.msg goto :finish
goto :waitstart
:test
REM rename the existing test.msg to idle.msg, hint: Use rename approach to save effort on file system.
ren genScripts\test.msg idle.msg
echo
echo The test needs a moment, here a ping is executed which needs some time.
echo abort: ctrl-C
::pause
REM the ping needs about 2 second on Windows, instead the test.
ping 127.0.0.1
REM if test is finished, send step to StimuliSelector.
REM it is possible because the StimuliSelector has a message queue
REM and it may be faster as a file as semaphore.
echo test finished, sends "step" via socket:
libs\socketcmd.exe -own:127.0.0.1:0xaff1 -dst:127.0.0.1:0xaff0 -cmd step -rx test abort finish -info
echo socketcmd.exe returns with %errorlevel%:
if errorlevel 1 goto :test
if errorlevel 2 goto :abort
if errorlevel 3 goto :finish
echo faulty
pause
REM then waits for file semaphore to run the next test.
goto :loop
:abort
move genScripts\abort.msg genScripts\idle.msg
echo test was aborted
::pause
goto :loop
exit /b
:finish
move genScripts\finish.msg genScripts\idle.msg
echo test series finished
::pause
goto :loop
exit /b

