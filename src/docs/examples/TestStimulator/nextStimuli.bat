REM any key sends "next"
:loop

libs\socmd.exe -own:127.0.0.1:0xaff1 -dst:127.0.0.1:0xaff0 step
echo abort: ctrl-C
pause
goto :loop
