echo off
REM any key sends "next"
libs\socketcmd.exe
:loop
echo first wait for cmd from Stimuli Selection
libs\socketcmd.exe -own:127.0.0.1:0xaff1 -dst:127.0.0.1:0xaff0 -rx test abort finish
if errorlevel 1 goto :test
if errorlevel 2 goto :abort
if errorlevel 3 goto :finish

:test
echo
echo The test needs a moment, here for demonstration press any key if test is finished.
echo abort: ctrl-C
::pause
REM the ping needs about 2 second, instead the test.
ping 127.0.0.1
REM if test is finished, send step to StimuliSelctor.
libs\socketcmd.exe -own:127.0.0.1:0xaff1 -dst:127.0.0.1:0xaff0 -cmd step
goto :loop
:abort
echo test was aborted
exit /b
:finish
echo test finished
exit /b

