REM for standalone usage, the libs are beside:
set LIBSPATH=./libs
REM for usage inside the cmpJava_vishia source tree:
if exist ..\..\..\..\tools set LIBSPATH=../../../../tools

set CP=%LIBSPATH%/vishiaGui.jar;%LIBSPATH%/vishiaBase.jar
REM comment out swt.win32_x86_32.jar or swt.win32_x86_64.jar
::set CP=%CP%;%LIBSPATH%/org.eclipse.swt.win32_x86_32.jar                                                    
set CP=%CP%;%LIBSPATH%/org.eclipse.swt.win32_x86_64.jar
REM hint: use java if something is wrong, to see outputs, 
REM       use javaw for normal usage without back cmd window
set JAVAW=java
echo dir=%CD%
REM call the GUI. This file %0 is used as argument for SimSelector. 
REM It contains all control after the JZtxtcmd label
echo on 
%JAVAW% -cp %CP% org.vishia.gral.cfg.appl.GuiDropFiles --@GuiDropFiles.args       
echo off
pause
exit /b
