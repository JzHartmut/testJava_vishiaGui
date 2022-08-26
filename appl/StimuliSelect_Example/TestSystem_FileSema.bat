echo off
:loop
echo first wait for presence of the semaphore file from Stimuli Selection
echo ... expected: genScripts\test.msg, ...\abort.msg or ...\finish.msg
:waitstart
if exist genScripts\test.msg goto :test
if exist genScripts\abort.msg goto :abort
if exist genScripts\finish.msg goto :finish
libs\socketcmd.exe -to:2000
goto :waitstart
:test
echo
echo The test needs a moment, here a ping is executed which needs some time.
echo ....test is running 3 seconds
type genScripts\testfile_text.txt
libs\socketcmd.exe -to:3000
echo test finished, rename step.msg:
ren genScripts\test.msg step.msg

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

