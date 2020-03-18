REM it creates the links to TMP and the directories in TMP

REM do nothing if build exists, as link or direct.
REM Note: removing only the build link is sufficient to clean all.
if exist build exit /b

REM TMP should be set in windows, it may refer a RAM disk
REM only emergency if TMP is not set:
if "%TMP%"=="" ( 
  set TMP=c:\tmp
  mkdir c:\tmp
)

REM The used temporary inside %TMP%
set TD=%TMP%\Test_JavaVishiaGui

REM clean content if build is not existing, and link
if not exist build (
  REM Note rmdir /S/Q does not remove files under a found link inside
  REM       but rmdir /S/Q %TD%\build\src would be remove all sources, unfortunately
  REM Note: rmdir /S/Q cleans all, del /S/Q/F does not clean the directory tree
  if exist %TD%\build rmdir /S/Q %TD%\build 
  mkdir %TD%\build
  mklink /J build %TD%\build
) 

if not exist .gradle (
  if exist %TD%\.gradle rmdir /S/Q %TD%\.gradle 
  mkdir %TD%\.gradle
  mklink /J .gradle %TD%\.gradle
)
exit /b

