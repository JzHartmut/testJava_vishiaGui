package org.vishia.gral.test.basics;

import java.text.ParseException;

import org.vishia.communication.Address_InterProcessComm_Socket;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.ExcUtil;

public class Test_GralTableAppl {

  
  static class TableData {
    final String name;
    int nrof;
    String value;
    
    public TableData(String name) {
      this.name = name;
    }
    
  }
  
  
  
  final LogMessage log;
  
  final GralWindow window;
  
  final GralMng gralMng;

  final GralTextField inputText;
  
  final GralTable<TableData> myTable;
  
  
  
  Test_GralTableAppl ( ) {
    this.log = new LogMessageStream(System.out);
    try {
      this.gralMng = new GralMng(this.log);
      //this.gralMng.setPos("@screen,10,20,80,120");
      int windProps = GralWindow_ifc.windRemoveOnClose | GralWindow_ifc.windResizeable;
      this.window = new GralWindow(this.gralMng.currPos(), "@screen,10+30,20+80=tableTestWin", "Test_GralTableAppl", windProps);
      //
      this.inputText = new GralTextField(this.gralMng.currPos(), "@2+2, 2+20=input", GralTextField.Type.editable);
      GralMenu inputFieldMenu = this.inputText.getContextMenu();
      inputFieldMenu.addMenuItem("&test1", null);
      inputFieldMenu.addMenuItem("&test2", null);
      
      int[] columns = {20,10,-20};
      this.myTable = new GralTable<TableData>(this.gralMng.currPos(), "@4+20,2..-20=myTable", 20, columns );
      
    } catch (Exception e) { 
      throw new RuntimeException("Unexpected" , e);
    }
    
  }
  
  
  void initImplGraphic() {
    GralFactory factory = new SwtFactory();
    factory.createGraphic(this.gralMng, 'C');
  }
  

  String[][] sTableData = 
  { { "R1", "10 kOhm", "5" }
  , { "R2", "15 kOhm", "4" }
  , { "R3", "22 kOhm", "8" }
  , { "R4", "47 kOhm", "23" }
  , { "R5", "4.7 Ohm", "18" }
  , { "R6", "68 Ohm",  "6" }
  , { "R7", "1 MOhm",  "3" }
  , { "R8", "220 kOhm","1" }
  , { "R9", "270 kOhm","1" }
  , { "C1", "22 pF",   "3" }
  , { "C2", "3.3 nF 100V",   "12" }
  , { "C3", "220 uF 20V",   "8" }
    
  };
  
  
  
  
  void addSomeLines() {
    for(String[] lineText : sTableData) {
      GralTableLine_ifc line = this.myTable.addLine(lineText[0], lineText, null);
    }
  }
  
  
  
  public static void main(String[] args) {
    Test_GralTableAppl thiz = new Test_GralTableAppl();
    thiz.initImplGraphic();
    thiz.myTable.setVisible(true);
    thiz.addSomeLines();
    while(!thiz.window.isGraphicDisposed()) {
      try { Thread.sleep(100); } catch (InterruptedException e) { }
    }
  }
}
