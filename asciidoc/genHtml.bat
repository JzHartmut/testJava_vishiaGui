:loop
::call C:\Programs\Asciidoc\genAsciidoc2Html.bat doc_emC/OSHAL/Serial_HAL.adoc ../../../../html/OSHAL
call C:\Programs\Asciidoc\genAsciidoc2Html.bat GUItools/StimuliSel.adoc ../../../../../StimuliSel/html
echo done
pause
goto :loop

