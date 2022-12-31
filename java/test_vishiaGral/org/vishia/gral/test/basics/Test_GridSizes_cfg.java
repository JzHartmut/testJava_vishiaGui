package org.vishia.gral.test.basics;

import org.vishia.guiViewCfg.ViewCfg;
import org.vishia.util.Debugutil;

public class Test_GridSizes_cfg {

  
  
  String sDir = "";
  
  
  String[] callArgs = 
  { "-gui=guiCfg/gridsizes.cfg"
  , "-SWT"
  , "-size:C"
  , "-title:Size: C"
  };
  
  
  /**Executes in a loop all configs in #testArg
   * 
   */
  public void executeLoop() {
    //
    for(char cSize = 'A'; cSize <='H'; ++cSize) {
      this.callArgs[2] = "-size:" + cSize;
      this.callArgs[3] = "-title:Size: " + cSize;
      ViewCfg.smain(this.callArgs);
    }
  }
  
  public static void main(String[] args) {
    Test_GridSizes_cfg thiz = new Test_GridSizes_cfg();
    if(args.length >0) { thiz.sDir = args[0]; }
    thiz.executeLoop();
  }

}
