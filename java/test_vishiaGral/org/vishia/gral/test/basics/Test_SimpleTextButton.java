package org.vishia.gral.test.basics;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;

/**This is a simple example for the Gral concept
 * showing an input field, a button and a output field.
 * @author Hartmut Schorrig, LPGL License
 *
 */
//tag::classAndActions[]
public class Test_SimpleTextButton {
  
  GralUserAction actionClean = new GralUserAction("actionButton") {
    @Override public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params ) {
      Test_SimpleTextButton.this.wdgInputText.setText("");
      return true;
    }
  };
  
  GralUserAction actionButton = new GralUserAction("actionButton") {
    @Override public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params ) {
      Test_SimpleTextButton.this.actionButton();
      return true;
    }
  };
  //end::classAndActions[]
  
  //tag::elements[]
  final LogMessage log;
  
  final GralMng gralMng = new GralMng(null);

  GralPos currPos = new GralPos(this.gralMng);
  
  final GralWindow window = new GralWindow(this.currPos, "@screen,10+30,20+80=panelWin"
                          , "Test_SimpleTextButton"
                          , GralWindow.windRemoveOnClose | GralWindow.windResizeable);
  
  final GralTextField wdgInputText = new GralTextField(this.currPos, "@panel, 2+2, 2+20=input"
                                   , GralTextField.Type.editable);
  
  final GralButton wdgButton = new GralButton(this.currPos, "@8-3, 2+10=button"
                             , "press me", this.actionButton);
  //end::elements[]
  
  //tag::ctor[]
  Test_SimpleTextButton ( ) {
    this.log = new LogMessageStream(System.out);
    this.gralMng.setLog(this.log);
    try {
      this.window.reportAllContent(this.log);
      addContextMenuToTextField();
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
    Test_SimpleTextButton thiz = new Test_SimpleTextButton();
    thiz.initImplGraphic();
    while(!thiz.window.isGraphicDisposed()) {
      try { Thread.sleep(100); } catch (InterruptedException e) { }
    }
  }
  //end::main[]

  //tag::initImplGraphic[]
  void initImplGraphic() {
    GralFactory factory = new SwtFactory();
    factory.createGraphic(this.gralMng, 'C');
  }
  //end::initImplGraphic[]
  
}
