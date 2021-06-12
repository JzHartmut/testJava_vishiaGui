REM starts as windows command (batch) file:

set LIBSPATH="../../../.."
set CP="%LIBSPATH%/libs/vishiaGui.jar;%LIBSPATH%/libs/vishiaBase.jar;%LIBSPATH%/libs/org.eclipse.swt.win32.win32.x86_64.jar"                                                    
set JAVAW=java
::cd ..\..\..\..
echo dir=%CD%
REM call the GUI. This file %0 is used as argument for SimSelector. It contains all control after the JZtxtcmd label
echo on 
%JAVAW% -cp %CP% org.vishia.stimuliSelector.StimuliSelector %0 -size:C       
echo off
pause
exit /b

                            
==JZtxtcmd==

currdir=<:><&scriptdir><.>;

include stimuliTables.jzTc;
include testfile_text.jzTc;
include testfile_xml.jzTc;

                                                                                  

sub btnGenSelection ( Map line1, Map line2, Map line3, Map line4, Map line5, Map line6) {
  <+out><&scriptdir>/<&scriptfile>: btnGenSelection ( 
     <&line1.name>, <&line2.name>) ..... <.+n>; 
  call genTestfiles(values=line1, texts=line2);
}
     




sub genTestfiles(Map values, Map texts) {

  String title = <:><&texts.name>_<&values.name><.>;     ## build the title
  mkdir genScripts;
  Openfile fText = "genScripts/testfile_text.txt";
  <+fText><:call:testfile_text : title=title, values=values, texts=texts><.+>
  fText.close();
  Openfile fXml = "genScripts/testfile_xml.xml";
  <+fXml><:call:testfile_xml : title=title, values=values, texts=texts><.+>
  fXml.close();
}


Obj soRx;
String cmpNext = <:>"step"<.>;

sub genTestcases(String select) {
  <+out>soRx=<&jztc.scriptVariables().soRx><.+n>
  if(jztc.scriptVariables().soRx) {    ##hint: use definitely the script variable, not the local copy.
    <+out>...abort genTestCases: <.+n> 
    jztc.scriptVariables().soRx.close();
    jztc.scriptVariables().soRx = null;
  } 
  else {
    <+out>generate test cases: .... <.+n> 
    Obj testcases = java org.vishia.testutil.TestConditionCombi.prepareTestCases(select, 5); 
    Thread execThread = {
      debug;
      jztc.scriptVariables().soRx = java new org.vishia.communication.GetRx_InterProcessComm("UDP:127.0.0.1:45040");
      <+out>Thread .... <&jztc.scriptVariables().soRx><.+n> 
      for(testcase: testcases) {
        String name = <:><:for:var:testcase><&var.sel><:hasNext>_<.hasNext><.for><.>; 
        <+out>test case: <&name><.+n> 
        String next = jztc.scriptVariables().soRx.waitRx();
        <+out>msg from socket::<&next>::<.+n>
        if(NOT (next == cmpNext)) { break; }
      }  
      jztc.scriptVariables().soRx.close();
      jztc.scriptVariables().soRx = null;
      <+out>Thread finished<.+n>
    }
    ##execThread.join();
  }
}







class ToGui 
{
  List tdata1 = values;
  List tdata2 = texts;
  List tdata3 = var_A;
  List tdata4 = var_B;
  List tdata5 = var_C;
}

                