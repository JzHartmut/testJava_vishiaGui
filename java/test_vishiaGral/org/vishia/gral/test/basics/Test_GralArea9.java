package org.vishia.gral.test.basics;

import org.vishia.gral.base.GralArea9Panel;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWindow;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;

public class Test_GralArea9 {

  
  
  final LogMessage log;
  
  final GralMng gralMng = new GralMng(new LogMessageStream(System.out));

  //GralPos currPos = new GralPos(this.gralMng);
  
  final GralWindow window = this.gralMng.addWindow("@screen,10+30,20+80=mainWin" , "Test_Area9");
  
  //final GralPanelContent panel2 = this.gralMng.addPanel("@mainWin,0..0, 0..0 = main2");
  final GralArea9Panel area9 = this.gralMng.addArea9Panel("@mainWin=main2");
  
  final GralTextBox textbox = this.gralMng.addTextBox("@main2,C1C3 =out", true, null, 't');                 //area9,C1C3=out");

  public Test_GralArea9() {
    this.log = new LogMessageStream(System.out);
    this.gralMng.setLog(this.log);
    try {
      this.window.reportAllContent(this.log);
//      addContextMenuToTextField();
    } catch (Exception e) { 
      throw new RuntimeException("Unexpected" , e);
    }

    
  }
  
  
  //tag::main[]
  public static void main(String[] args) {
    Test_GralArea9 thiz = new Test_GralArea9();
    thiz.initGraphic();
    thiz.textbox.setText("TextC1C3");
    while(!thiz.window.isGraphicDisposed()) {
      try { Thread.sleep(100); } catch (InterruptedException e) { }
    }
  }
  //end::main[]


  //tag::initImplGraphic[]
  void initGraphic() {
    this.gralMng.createGraphic("SWT", 'E', log);
  }
  //end::initImplGraphic[]

}
