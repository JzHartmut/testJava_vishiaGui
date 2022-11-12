package org.vishia.gral.test.basics;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;

public class Test_WindMenu {

  
  GralUserAction actionClean = new GralUserAction("actionClean") {
    @Override public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params ) {
      Test_WindMenu.this.wdgInputText.setText("");
      return true;
    }
  };
  
  GralUserAction actionButton = new GralUserAction("actionButton") {
    @Override public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params ) {
      Test_WindMenu.this.actionButton();
      return true;
    }
  };
  //end::classAndActions[]
  
  //tag::elements[]
  final GralMng gralMng = new GralMng(new LogMessageStream(System.out));

  //GralPos currPos = new GralPos(this.gralMng);
  
  final GralWindow window = gralMng.addWindow("@screen,10+30,20+80=panelWin"
                          , "Test_WindMenu");
  
  final GralMenu menuWind = this.window.getMenuBar();
  
  
  
  final GralTextField wdgInputText = gralMng.addTextField("@panel, 2+2, 2+20=input"
                                   , true, null, null);
  
  final GralButton wdgButton = this.gralMng.addButton("@8-3, 2+10=button"
                             , this.actionButton, "press me");
  //end::elements[]
  
  //tag::ctor[]
  Test_WindMenu ( ) {
    try {
      addContextMenuToTextField();
      this.menuWind.addMenuItem("revert", "&test/&revers", this.actionButton);
      this.menuWind.addMenuItem("clean", "&test/&clean", this.actionButton);
      this.menuWind.addMenuItem("clean2", "&clean/&clean", this.actionButton);
      this.window.reportAllContent(this.gralMng.log);
    } catch (Exception e) { 
      throw new RuntimeException("Unexpected" , e);
    }
  }
  //end::ctor[]
  
  //tag::addContextMenuToTextField[]
  void addContextMenuToTextField ( ) {
    GralMenu inputFieldMenu = this.wdgInputText.getContextMenu();
    inputFieldMenu.addMenuItem("&revers", this.actionButton);
    inputFieldMenu.addMenuItem("&clean", actionClean);
  }
  //end::addContextMenuToTextField[]
  
  
  void actionButton() {
    String text = this.wdgInputText.getText();
    StringBuilder newText = new StringBuilder(text.length());
    for(int pos = text.length()-1; pos>=0; --pos) {
      newText.append(text.charAt(pos));
    }
    this.wdgInputText.setText(newText);
  }
  

  //tag::main[]
  public static void main(String[] args) {
    Test_WindMenu thiz = new Test_WindMenu();
    String sAwtOrSwt = args.length >0 && args[0].length() >0 ? args[0] : "SWT";
    thiz.initGraphic(sAwtOrSwt);
    while(!thiz.window.isGraphicDisposed()) {
      try { Thread.sleep(100); } catch (InterruptedException e) { }
    }
  }
  //end::main[]

  //tag::initImplGraphic[]
  void initGraphic(String awtOrSwt) {
    this.wdgInputText.setText("revers");
    this.gralMng.createGraphic(awtOrSwt, 'E', null);
  }
  //end::initImplGraphic[]

}
