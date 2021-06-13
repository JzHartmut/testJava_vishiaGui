REM starts as windows command (batch) file:

set LIBSPATH="../"
set CP=%LIBSPATH%/libs/vishiaGui.jar;%LIBSPATH%/libs/vishiaBase.jar
set CP=%CP%;%LIBSPATH%/libs/org.eclipse.swt.win32.win32.x86_64.jar                                                    
set JAVAW=java
echo dir=%CD%
REM call the GUI. This file %0 is used as argument for SimSelector. 
REM It contains all control after the JZtxtcmd label
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


                                                                                  
##
##The button routine for [Gen Selection]
##The arguments are the currently selected lines.
##
sub btnGenSelection ( Map line1, Map line2, Map line3, Map line4, Map line5, Map line6) {
  <+out><&scriptdir>/<&scriptfile>: btnGenSelection (<: > 
     <&line1.name>, <&line2.name>) ..... <.+n>; 
  call genTestfiles(values=line1, texts=line2);
}
     



##
##This is the generation routine for one test case, 
##either for manual [gen selection] or used for [gen test cases]
##
sub genTestfiles(Map values, Map texts) {

  String title = <:><&texts.name>_<&values.name><.>;     ## build the title
  mkdir genScripts;
  String sfText = "genScripts/testfile_text.txt";
  Openfile fText = sfText;
  <+fText><:call:testfile_text : title=title, values=values, texts=texts><.+>
  fText.close();
  <+out>gen: <&sfText><.+n>
  String sfXml = "genScripts/testfile_xml.xml";
  Openfile fXml = sfXml;
  <+fXml><:call:testfile_xml : title=title, values=values, texts=texts><.+>
  fXml.close();
  <+out>gen: <&sfXml><.+n>
}



##expected command from SocketCmd_vishia to execute the next step for gen test cases.
##
String cmpNext = <:>"step"<.>;




##
##This routine is the button routine for the [gen testcases] button.
##Here it starts another thread which generates in loop step by step 
##  after receiving a "next" command from UDP communication (using socketCmd_vishia.exe)
##If this routine is invoked secondly (press button secondly) and the thread is active
##  then the UDP socket connection is closed to abort the generation thread.
##
sub btnGenTestcases(String select) {
 
  <+out>soRx=<&jztc.envar.soRx><.+n>
  if(jztc.envar.soRx) {    ##hint: use definitely the script variable, not the local copy.
    <+out>...abort genTestCases: <.+n> 
    jztc.envar.soRx.close();
    jztc.envar.soRx = null;
  } 
  else {
    <+out>generate test cases: .... <.+n> 
    Obj testcases = java org.vishia.testutil.TestConditionCombi.prepareTestCases(select, 5);
    call genTestCaseThread(testcases = testcases);
  }
}



##
##The genTestCases thread.
##Hint: The sub routine is the wrapper arround the thread.
##      The sub routine itself is finished immediately, necessary because it is calling in the GUI thread.
##
sub genTestCaseThread(Obj testcases) {
  Thread execThread = {                        ## This thread generates one test case in each for loop
    jztc.envar.soRx = java new org.vishia.communication.GetRx_InterProcessComm("UDP:127.0.0.1:45040");
    <+out>Thread .... <&jztc.envar.soRx><.+n> 
    Bool contFor = true;                       ## possibility to abort the generation
    for(testcase: testcases && contFor ) {
      String name = <:><:for:var:testcase><&var.sel><:hasNext>_<.hasNext><.for><.>; 
      <+out>test case: <&name><.+n> 
      Obj lineValues = values.get(testcase[0].sel);
      Obj lineTexts = texts.get(testcase[1].sel);          ## generates the files for this case:
      call genTestfiles(values = lineValues, texts = lineTexts);
      ##
      String next = jztc.envar.soRx.waitRx();  ## waits for a cmd received via socket:
      <+out>msg from socket::<&next>::<.+n>
      contFor = bool(next >= cmpNext);         ## repeats, generate next if "step" is received
    }  
    jztc.envar.soRx.close();
    jztc.envar.soRx = null;
    <+out>Thread finished<.+n>
  }
  ##do not use execThread.join(0); because the wrapper routine should be immediately finished, called in the GUI thread!
}



##
##This class defines which tables should be used in the StimuliSelector GUI
##
class ToGui 
{
  List tdata1 = values;
  List tdata2 = texts;
  List tdata3 = var_A;
  List tdata4 = var_B;
  List tdata5 = var_C;
}

