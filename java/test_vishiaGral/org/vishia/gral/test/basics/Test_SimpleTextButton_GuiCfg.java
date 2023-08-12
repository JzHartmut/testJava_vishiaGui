//tag::import_class[]
package org.vishia.gral.test.basics;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GuiCallingArgs;
import org.vishia.gral.cfg.GuiCfg;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;

/**This class is a simple example for a Gral GUI application with a hard coded program
 * (not textual configured). 
 * It uses nevertheless the base class {@link GuiCfg} which is firstly for textual graphic configuration.
 * But this features don't need to be used. 
 * The GuiCfg builds a base graphic system which is able to enhance also with program parts.
 * 
 * @author Hartmut Schorrig
 * @since 2023-08
 */
public class Test_SimpleTextButton_GuiCfg extends GuiCfg {
//end::import_class[]

//tag::widgets[]
  /**An application widget which should be accessed in the process of working with the GUI. 
   * Here a text for input/output*/
  final GralTextField wdgInputText;

  /**An application widget which may be accessed in the process of working with the GUI.
   * But if it is not accessed the reference is not necessary to store. 
   * Here a button with a operation while pressing. The access itself is not necessary. */
  final GralButton wdgButton;
//end::widgets[]

  
//tag::ctor[]
  /**The constructor of the application class organizes some more widgets
   * in a programmed way.
   * @param cargs Arguments, only {@link GuiCallingArgs#sTitle} is used.
   */
  Test_SimpleTextButton_GuiCfg (GuiCallingArgs cargs) {
    super(cargs, null, null, null);              // It creates the main window
    //                                           //  with the given title and properties.
    this.wdgInputText = new GralTextField(super.refPos, "@2+2, 2+20=input"
        , GralTextField.Type.editable);          // add a widget to the main panel.
    this.wdgInputText.setText("abcd");           // set initial a text.
    //                                           // add the button widget
    this.wdgButton = new GralButton(this.refPos, "@8-3, 2+10=button"
        , "press me", this.actions.actionButton);// with this given action on pressing
  }
//end::ctor[]
  
  
  
//#tag::overriddenOps[]
  /**This operation is called to create the graphic appearance. 
   * Some more things can be done in the main thread after the ctor if necessary,
   * for example fill some widgets with data.
   * The super implementation creates the graphic, should be called here at least.
   */
  @Override public void initMain ( ) {
    super.initMain();        // creates the graphic appearance.
  }
  
  @Override public void stepMain () {
    // nothing to do, called every 20 ms
    // here any back activity is possible, it is the main thread.
    super.stepMain();        // it is also empty.
  }
  
  /**This operation is called on finishing the GUI application,
   * after the window is removed.
   */
  @Override public void finishMain () {
    super.finishMain();      // looks only for a user for configuration and closes it,
  }                          // empty for this application.
  
//#end::overriddenOps[]
  
//tag::process_actionButton[]
  /**Action for the button, called from {@link Actions#actionButton},
   * associated to the #wdgButton.
   * It reads the text, change the order and writes back.  */
  void actionButton() {
    String text = this.wdgInputText.getText();   // reads the text
    StringBuilder newText = new StringBuilder(text.length());
    for(int pos = text.length()-1; pos>=0; --pos) {
      newText.append(text.charAt(pos));
    }                                            // and writes back in revers order
    this.wdgInputText.setText(newText);          // only as example
  }
//end::process_actionButton[]
  
  

  //tag::main[]
  public static void main(String[] cmdArgs) {
    GuiCallingArgs cargs = new GuiCallingArgs();           // The cargs instance is necessary           
    boolean bOk = cargs.parseArgs(cmdArgs, System.err);    // parse command line args if given, not necessary for this example
    if(bOk) {                                              // set a title for the main window if not given
      if(cargs.sTitle == null) { cargs.sTitle = "Test_SimpleTextButton_GuiCfg"; }  
      Test_SimpleTextButton_GuiCfg thiz = new Test_SimpleTextButton_GuiCfg(cargs); //ctor
      thiz.execute();   // calls initMain(), stemMain() and waits for closing graphic, then finishMain().
    }
  }
  //end::main[]

  
  //tag::Actions_class[]
  /**Actions may be organized in an extra class, more overview in Outline of the class and debug variables.
   */
  private class Actions {
    
    /**Action for the button, calls {@link Test_SimpleTextButton_GuiCfg#actionButton()}*/
    GralUserAction actionButton = new GralUserAction("actionButton") {
      @Override public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params ) {
        Test_SimpleTextButton_GuiCfg.this.actionButton();
        return true;
      }
    };
    Actions() {}      
    
  } // class Actions
  private Actions actions = new Actions();       // instance of actions
  //end::Actions_class[]
  
}
