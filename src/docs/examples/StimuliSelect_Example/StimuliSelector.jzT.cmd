REM starts as windows command (batch) file:

set LIBSPATH=./
set CP=%LIBSPATH%/libs/vishiaGui.jar;%LIBSPATH%/libs/vishiaBase.jar
set CP=%CP%;%LIBSPATH%/libs/org.eclipse.swt.win32.win32.x86_64.jar                                                    
REM hint: use java if something is wrong, to see outputs, 
REM       use javaw for normal usage without back cmd window
set JAVAW=javaw
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
sub genTestfiles(String name = "testfile", Map values, Map texts) {

  String title = <:><&texts.name>_<&values.name><.>;     ## build the title
  mkdir genScripts;
  String sfText = <:>genScripts/<&name>_text.txt<.>;
  Openfile fText = sfText;
  <+fText><:call:testfile_text : title=title, values=values, texts=texts><.+>
  fText.close();
  <+out>gen: <&sfText><.+n>
  String sfXml = <:>genScripts/<&name>_xml.xml<.>;
  Openfile fXml = sfXml;
  <+fXml><:call:testfile_xml : title=title, values=values, texts=texts><.+>
  fXml.close();
  <+out>gen: <&sfXml><.+n>
}


##
##This routine is the button routine for the [gen testcases] button.
##
sub btnGenTestcases ( String select) {
  call btnGenTestcases_M(select=select);
  ##call btnGenTestcases_A(select=select);   ##generate all files with different names
  ##call btnGenTestcases_M(select=select);   ##use socket messages
  ##call btnGenTestcases_F(select=select);   ##use file semaphores
}  
 


##
##This routine is the button routine for the [gen testcases] button
##for generation all files.
##
sub btnGenTestcases_A ( String select) {
  Obj testcs = java org.vishia.testutil.TestConditionCombi.prepareTestCases( select, 5);
  for(testcase: testcs ) {
    String name = <:><:for:var:testcase><&var.sel><:hasNext>_<.hasNext><.for><.>; 
    <+out>test case: <&name><.+n>
    Obj lineValues = values.get(testcase[0].sel);
    Obj lineTexts = texts.get(testcase[1].sel); ## generates the files for this case:
    call genTestfiles(name = name, values = lineValues, texts = lineTexts);
  }
}



##
##This routine is the button routine for the [gen testcases] button.
##Here it starts another thread which generates in loop step by step 
##  after receiving a "next" command from UDP communication (using socketCmd_vishia.exe)
##If this routine is invoked secondly (press button secondly) and the thread is active
##  then the UDP socket connection is closed to abort the generation thread.
##
sub btnGenTestcases_M ( String select) {
  if(jztc.envar.soRx) {    ##hint: special variable inside Java wrapper.
    <+out>...abort genTestCases: <.+n> 
    jztc.envar.soRx.tx("abort");
    jztc.envar.soRx.close();
    jztc.envar.soRx = null;
  } 
  else {
    <+out>generate test cases: .... <.+n> 
    Thread execThread = {         ## This thread generates one test case in each for loop
      call genTestCaseThread_M(select=select);
    }
    ##do not use: execThread.join(0); 
    ##because the wrapper routine should be immediately finished, 
    ##it is called in the GUI thread!
  }
}




##
##This routine is the button routine for the [gen testcases] button.
##Here it starts another thread which generates in loop step by step 
##  after receiving a "next" command from UDP communication (using socketCmd_vishia.exe)
##If this routine is invoked secondly (press button secondly) and the thread is active
##  then the UDP socket connection is closed to abort the generation thread.
##
sub btnGenTestcases_F ( String select) {
  if(jztc.envar.soRx) {    ##hint: special variable inside Java wrapper.
    <+out>...abort genTestCases: <.+n> 
    jztc.envar.soRx.tx("abort");
    FileSystem.renameCreate(File: "genScripts", "*.msg", "abort.msg", 1);
    jztc.envar.soRx.close();
    jztc.envar.soRx = null;
  } 
  else {
    <+out>generate test cases: .... <.+n> 
    Thread execThread = {         ## This thread generates one test case in each for loop
      call genTestCaseThread_F(select=select);
    }
    ##do not use: execThread.join(0); 
    ##because the wrapper routine should be immediately finished, 
    ##it is called in the GUI thread!
  }
}






##
##The genTestCases thread.
##Hint: The sub routine is the wrapper arround the thread.
##      The sub routine itself is finished immediately, necessary because it is calling 
##      in the GUI thread.
##
sub genTestCaseThread_M(String select) {
  String sIpOwn="UDP:127.0.0.1:45040";
  String sIpDst="UDP:127.0.0.1:45041";
  jztc.envar.soRx = java new org.vishia.communication.SocketCmd_InterProcessComm
                                                      (sIpOwn, sIpDst);
  ##FileSystem.renameCreate(File: "genScripts", "*.msg", "idle.msg", 1);
  Bool contFor = true;                       ## possibility to abort the generation
  Obj testcs = java org.vishia.testutil.TestConditionCombi.prepareTestCases( select, 5);
  Bool rxHasError = false;
  for(testcase: testcs && contFor ) {
    rxHasError = jztc.envar.soRx.hasError();   ## first time may be open error, 
    if(rxHasError) {                           ## faulty socket etc.
      <+out>ERROR socket receive on <&sIpOwn>: <&jztc.envar.soRx.getState()><.+n>
      contFor = false;
    } else {
      ##                                       ## waits for a cmd received via socket:
      String next = jztc.envar.soRx.waitRx();  ## from the test system.
      <+out>rx from Test System: <&next><.+n>
      contFor = bool(next >= "step");          ## repeats, generate next if "step" is received
      if(contFor) {
        ##                                     ## prepare one test file
        String name = <:><:for:var:testcase><&var.sel><:hasNext>_<.hasNext><.for><.>; 
        <+out>test case: <&name><.+n>
        if(jztc.envar.stimuliSelector) {  ##Hint: stimuliSelector is not given outside GUI
          jztc.envar.stimuliSelector.btnGenTestcases.setBackColor(jztc.envar.colorGenTestcaseActive, 0);
          jztc.envar.stimuliSelector.btnGenTestcases.setText("abort generate");
        }
        Obj lineValues = values.get(testcase[0].sel);
        Obj lineTexts = texts.get(testcase[1].sel); ## generates the files for this case:
        call genTestfiles(name = "testfile", values = lineValues, texts = lineTexts);
    
        ##
        ##Obj cmd1 = execThread.awaitcmd(1000);
        java java.lang.Thread.sleep(1000);
        jztc.envar.soRx.tx("test");            ## starts the test with msg to Test System
        ##FileSystem.renameCreate(File: "genScripts", "*.msg", "test.msg", 1);
        ##
        if(jztc.envar.stimuliSelector) {
          jztc.envar.stimuliSelector.btnGenTestcases.setBackColor(jztc.envar.colorGenTestcaseWaitRx, 0);
          jztc.envar.stimuliSelector.btnGenTestcases.setText("abort wait rx");
        }
      }
    }
  }  
  rxHasError = jztc.envar.soRx.hasError();
  if(rxHasError) {
    <+out>Thread aborted<.+n>
    jztc.envar.soRx.tx("error");
    ##FileSystem.renameCreate(File: "genScripts", "*.msg", "abort.msg", 1);
    if(jztc.envar.stimuliSelector) {
      jztc.envar.stimuliSelector.btnGenTestcases.setBackColor(jztc.envar.colorGenTestcaseError, 0);
    }
  } else {
    <+out>Thread finished<.+n>
    jztc.envar.soRx.tx("finish");
    ##FileSystem.renameCreate(File: "genScripts", "*.msg", "finish.msg", 1);
    if(jztc.envar.stimuliSelector) {
      jztc.envar.stimuliSelector.btnGenTestcases.setBackColor(jztc.envar.colorGenTestcaseInactive, 0);
    } 
  }
  if(jztc.envar.stimuliSelector) {
    jztc.envar.stimuliSelector.btnGenTestcases.setText("gen test cases");
  }
  jztc.envar.soRx.close();
  jztc.envar.soRx = null;
}



##
##The genTestCases thread.
##Hint: The sub routine is the wrapper arround the thread.
##      The sub routine itself is finished immediately, necessary because it is calling 
##      in the GUI thread.
##
sub genTestCaseThread_F(String select) {
  String sIpOwn="UDP:127.0.0.1:45040";
  String sIpDst="UDP:127.0.0.1:45041";
  jztc.envar.soRx = java new org.vishia.communication.SocketCmd_InterProcessComm
                                                      (sIpOwn, sIpDst);
  FileSystem.renameCreate(File: "genScripts", "*.msg", "idle.msg", 1);
  Bool contFor = true;                       ## possibility to abort the generation
  Obj testcases = java org.vishia.testutil.TestConditionCombi.prepareTestCases
                                                              (select, 5);
  Bool rxHasError = false;
  for(testcase: testcases && contFor ) {
    String name = <:><:for:var:testcase><&var.sel><:hasNext>_<.hasNext><.for><.>; 
    <+out>test case: <&name><.+n>
    if(jztc.envar.stimuliSelector) {  ##Hint: stimuliSelector is not given outside GUI
      jztc.envar.stimuliSelector.btnGenTestcases.setBackColor(jztc.envar.colorGenTestcaseActive, 0);
      jztc.envar.stimuliSelector.btnGenTestcases.setText("abort generate");
    }
    Obj lineValues = values.get(testcase[0].sel);
    Obj lineTexts = texts.get(testcase[1].sel); ## generates the files for this case:
    call genTestfiles(name = "testfile", values = lineValues, texts = lineTexts);

    ##
    ##Obj cmd1 = execThread.awaitcmd(1000);
    java java.lang.Thread.sleep(1000);
    jztc.envar.soRx.tx("test");
    FileSystem.renameCreate(File: "genScripts", "*.msg", "test.msg", 1);
    ##
    rxHasError = jztc.envar.soRx.hasError();   ## first time may be open error, 
    if(rxHasError) {                           ## faulty socket etc.
      <+out>ERROR socket receive on <&sIpOwn>: <&jztc.envar.soRx.getState()><.+n>
      contFor = false;
    } else {                                   ## waits for a cmd received via socket:
      if(jztc.envar.stimuliSelector) {
        jztc.envar.stimuliSelector.btnGenTestcases.setBackColor(jztc.envar.colorGenTestcaseWaitRx, 0);
        jztc.envar.stimuliSelector.btnGenTestcases.setText("abort wait rx");
      }
      <+out>Thread waits for <&sIpOwn>: <&jztc.envar.soRx><.+n> 
      String next = jztc.envar.soRx.waitRx();  ## <-- here waits for a cmd
      <+out>msg from socket::<&next>::<.+n>
      contFor = bool(next >= "step");          ## repeats, generate next if "step" is received
    }
  }  
  rxHasError = jztc.envar.soRx.hasError();
  if(rxHasError) {
    <+out>Thread aborted<.+n>
    jztc.envar.soRx.tx("error");
    FileSystem.renameCreate(File: "genScripts", "*.msg", "abort.msg", 1);
    if(jztc.envar.stimuliSelector) {
      jztc.envar.stimuliSelector.btnGenTestcases.setBackColor(jztc.envar.colorGenTestcaseError, 0);
    }
  } else {
    <+out>Thread finished<.+n>
    jztc.envar.soRx.tx("finish");
    FileSystem.renameCreate(File: "genScripts", "*.msg", "finish.msg", 1);
    if(jztc.envar.stimuliSelector) {
      jztc.envar.stimuliSelector.btnGenTestcases.setBackColor(jztc.envar.colorGenTestcaseInactive, 0);
    } 
  }
  if(jztc.envar.stimuliSelector) {
    jztc.envar.stimuliSelector.btnGenTestcases.setText("gen test cases");
  }
  jztc.envar.soRx.close();
  jztc.envar.soRx = null;
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

