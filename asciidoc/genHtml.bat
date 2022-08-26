:loop
::call C:\Programs\Asciidoc\genAsciidoc2Html.bat doc_emC/OSHAL/Serial_HAL.adoc ../../../../html/OSHAL
::call C:\Programs\Asciidoc\genAsciidoc2Html.bat GUItools/StimuliSel.adoc ../../../../../StimuliSel/html
call C:\Programs\Asciidoc\genAsciidoc2Html.bat GUItools/Gitcmd.adoc ../../../../../Git/html
call C:\Programs\Asciidoc\genAsciidoc2Html.bat GUItools/GitGui.adoc ../../../../../Git/html
::call C:\Programs\Asciidoc\genAsciidoc2Html.bat GUItools/FcmdNew.adoc ../../appl/Fcmd/Fcmd-help
echo done
pause
goto :loop

