package org.vishia.gral.test.appl;

import org.vishia.guiViewCfg.ViewCfg;
import org.vishia.util.Debugutil;

public class Test_GuiCfg {

  String sDir = "";
  
  String[] tests = 
  { "Tabtest1.cfg"
  , "TestLabel.cfg"
  , "Table.cfg"
  , "TestButton.cfg"
  //, "-@guiCfg/gui.args"
  };
  
  String[] callArgs = 
  { null
  , "-SWT"
  , "-size:C"
  , null
  , null
  };
  
  
  /**Executes in a loop all configs in #testArg
   * 
   */
  public void executeLoop() {
    callArgs[0] = "--@" + this.sDir + "guiCfg/gui.args";
    this.callArgs[2] = "-size:C";
    for(String testArg: this.tests) {
      this.callArgs[3] = "-gui=" + this.sDir + "guiCfg/gui" + testArg;
      this.callArgs[4] = "-logcfg=$(TMP)/TestGuiCfg/" + testArg + "cfglog.txt";
      ViewCfg.smain(callArgs);
      Debugutil.stop();
    }
    //
    for(char cSize = 'A'; cSize <='F'; ++cSize) {
      callArgs[2] = "-size:" + cSize;
      ViewCfg.smain(callArgs);
    }
  }
  
  public static void main(String[] args) {
    Test_GuiCfg thiz = new Test_GuiCfg();
    if(args.length >0) { thiz.sDir = args[0]; }
    thiz.executeLoop();
  }
  
}
