package org.vishia.gral.test.basics;

import java.io.File;
import java.io.IOException;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralFileProperties;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.gral.widget.GralViewFileContent;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.ExcUtil;
import org.vishia.util.KeyCode;

/**This class contains a simple example with a switching button, an input text field and a output
 * text field. When the button is pressed, the text of the input field will be read and an output will be written.
 * @author Hartmut Schorrig
 *
 */
//tag::classHead[]
public class Test_GralFileSelector {

  protected final LogMessage log;
  //end::classHead[]
  
  /**Version, history and license.
   * <ul>
   * <li>2022-01-26 Hartmut improved a little bit
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20220126;

  //tag::guiClass[]
  /**Extra inner class (for more data structuring) for all Gui elements.
   */
  protected class GuiElements
  {
  
    final GralMng gralMng = new GralMng(new LogMessageStream(System.out));             // on Gral widget structuring no log necessary. 
  
    GralPos refPos = new GralPos(this.gralMng);            // use an own reference position to build

    GralViewFileContent fileViewer = new GralViewFileContent(refPos, "@50..100, 50..100=fileViewer" + ".view");

    final GralWindow window = new GralWindow(this.refPos, "@10+30,20+80=mainWin"
                            , "Test GralFileSelector"
                            , GralWindow.windRemoveOnClose | GralWindow.windResizeable);
    
    final GralButton wdgButton1 = new GralButton(this.refPos, "@8-3, -8..-1 =button1"
        , "press me", Test_GralFileSelector.this.actionButton); //Position string: next to right with 2 units space

    
    final GralFileProperties wdgFileProps = GralFileProperties.createWindow(this.refPos, "@8+30, 30+52=fileProps", "file properties");
    
    final GralFileSelector wdgFileSelect = new GralFileSelector(this.refPos, "@0..-10, 0..-10=selectFile", null
        , 50, new int[]{2,0,-6,-12}, Test_GralFileSelector.this.cfg.bWithFavor, Test_GralFileSelector.this.cfg.sExecBtn
        , this.fileViewer, this.wdgFileProps);
    
    
    GralTextBox widgOutput = new GralTextBox(this.refPos, "@-10..0,0..0=output");
  
    GuiElements() { }                                      // empty ctor, only formally
  }
  //end::guiClass[]



  //tag::fieldsCtor[]
  /**Instance of inner class contains the graphical elements.*/
  protected final GuiElements gui;
  
  private static class Cfg {
    boolean bWithFavor;
    
    String sExecBtn;
  }
  
  protected Cfg cfg = new Cfg();
  
  char sizeGui = 'G';
  
  int ctKeyStroke1 = 0, ctKeyStroke2 = 0;
  
  Test_GralFileSelector ( String[] args)
  {
    this.cfg.bWithFavor = args[0] !=null && args[0].contains("f") ? true : false;
    this.cfg.sExecBtn   = args[0] !=null && args[0].contains("s") ? "Save as" : null;
    this.log = new LogMessageStream(System.out);  // may also write to a file, use calling arguments
    this.gui = new GuiElements();                 // initialize the graphic Gral Widgets (not the implementig graphic).
  }
  //end::fieldsCtor[]
  
  
  
  
  
  /**Code snippet for initializing the GUI. This snippet will be executed
   * in the graphic thread. It is an anonymous inner class. 
   */
  //tag::initImplGraphic[]
  void init(String awtOrSwt) {
    this.gui.gralMng.createGraphic(awtOrSwt, 'E', this.log);
    if(this.cfg.bWithFavor) {
      this.gui.wdgFileSelect.add("vishia", "D:/vishia");
      this.gui.wdgFileSelect.add("programs", "C:/Program Files");
      this.gui.wdgFileSelect.add("windows", "C:/Windows");
      this.gui.wdgFileSelect.add("Doc", "D:/docu");
      this.gui.wdgFileSelect.add("Doc1", "Q:/VIDEOS");
      this.gui.wdgFileSelect.add("tmp", "T:/tmp");
      this.gui.wdgFileSelect.add("Doc3", "D:/docu");
      this.gui.wdgFileSelect.setActionOnSaveButton(this.actionSave, "save as");
    }
    //FileRemote fileIn = FileRemote.fromFile(new File("D:/vishia"));
    //this.gui.wdgFileSelect.fillIn(fileIn, false);
  }
  //end::initImplGraphic[]

  
  //tag::execute[]
  /**execute routine for any other actions than the graphical actions. 
   * The application may do some things beside.
   */
  void execute()
  {
    //Now do nothing because all actions are done in the graphic thread.
    //A more complex application can handle some actions in its main thread simultaneously and independent of the graphic thread.
    //
    while(this.sizeGui >= 'A') {
      if(gui.wdgButton1.wasReleased()) {
        this.gui.widgOutput.append("Button2 " + (++this.ctKeyStroke2)  + "\n");
        this.gui.gralMng.closeImplGraphic();
      }
      try{ Thread.sleep(100); } catch(InterruptedException exc){}
      if(this.gui.gralMng.isImplementationGraphicTerminated()) {
        this.sizeGui -= 1;
        if(this.sizeGui >= 'A') {
          this.gui.gralMng.createGraphic("SWT", sizeGui, this.log);
        }
      }
    }
  }
  //end::execute[]



  public static String testMain = "currdir = <:>C:/<.>;\n"
      + "\n"
      + "main(){\n"
      + "  call testGralFileSelector();"
      + "}\n";
  
  public static String testScript = 
        "sub testGralFileSelector() {"
      + "<+out>test<.+n>;"
      + "Obj gralMng = new org.vishia.gral.base.GralMng(jztc.log());"
      + "Obj widg = java org.vishia.gral.widget.GralFileSelector.createGralFileSelectorWindow(\"@20+80,20+30=wind\", \"Test GralFileSelector Window\", gralMng);"
      + "gralMng.createGraphic(\"SWT\", \"C\", null);"
      + "Obj startDir = java org.vishia.fileRemote.FileRemote(File: \"C:/D\"); "
      + "widg.openDialog(startDir, \"Test GralFileSelector Window - open\", \"execute\", null);"
      + "while(gralMng.isRunning()){ sleep(10); }"
      + "}\n";
  
  private static void testWindow() {
    GralMng gralMng = new GralMng(new LogMessageStream(System.out));
    GralFileSelector widg = GralFileSelector.createGralFileSelectorWindow(gralMng, "@30+70,20+40=wind", "Test GralFileSelector Window", null); 
    gralMng.createGraphic("SWT", 'C', null);
    FileRemote startDir = FileRemote.fromFile(new File("C:/D"));
    widg.openDialog(startDir, "Test GralFileSelector Window - open", "execute", null, null);
    while(gralMng.isRunning()) { try { Thread.sleep(10);} catch(Exception exc) {}}
  }
  

  
  static void testGui ( String[] args){
    Test_GralFileSelector thiz = new Test_GralFileSelector(args); // constructs the main class
    thiz.init("SWT");
    thiz.execute();
  }
  

  /**The main routine. It creates the factory of this class
   * and then calls {@link #main(String[], Factory)}.
   * With that pattern a derived class may have a simple main routine too.
   * @param args command line arguments.
   */
  //tag::main[]
  public static void main(String[] args)
  {
    try {
      //testWindow();
      //org.vishia.jztxtcmd.JZtxtcmd.execute(testMain + testScript);
      testGui(args);
    } catch (Exception exc) {
      System.out.println(ExcUtil.exceptionInfo("unexpected", exc, 1, 10));
//      System.err.println("Exception: " + exc.getMessage());
//      exc.printStackTrace(System.err);
    }
  }
  //end::main[]


  
  
  /**Operation on button pressed, on the application level.
   * It uses the known references to the GralWidget. 
   * Immediately access to implementation widgets is not necessary.  
   * This operation is executed in the Graphic thread. 
   * Be carefully, do not program longer or hanging stuff such as synchronized or sleep.
   */
  void actionSave(File file) throws IOException {
    this.gui.widgOutput.append("Save >>" + file.getAbsolutePath() + "<<\n");
  }
  

  //tag::action[]
  /**Operation on button pressed, on the application level.
   * It uses the known references to the GralWidget. 
   * Immediately access to implementation widgets is not necessary.  
   * This operation is executed in the Graphic thread. 
   * Be carefully, do not program longer or hanging stuff such as synchronized or sleep.
   */
  void actionButton() throws IOException {
    this.gui.widgOutput.append("Button1 " + (++this.ctKeyStroke1) + " time, text=" + "\n");
  }
  
  
  /**Action operation is called in the event handler of the appropriate widget. */
  private final GralUserAction actionButton = new GralUserAction("buttonCode")
  { 
    @Override
    public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        try{ 
          Test_GralFileSelector.this.actionButton();         // defined of class level of the main (environment) class.
        } catch(Exception exc){                            // Exceptions should catch anyway. but not expected.
          Test_GralFileSelector.this.log.writeError("Unexpected", exc);
        }                           
      }
      return true;  
    } 
  };  
  //end::action[]

  
  /**Action operation is called in the event handler of the appropriate widget. */
  private final GralUserAction actionSave = new GralUserAction("actionSave")
  { 
    @Override
    public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        try{ 
          FileRemote file = (FileRemote)params[0];
          Test_GralFileSelector.this.actionSave(file);         // defined of class level of the main (environment) class.
        } catch(Exception exc){                            // Exceptions should catch anyway. but not expected.
          Test_GralFileSelector.this.log.writeError("Unexpected", exc);
        }                           
      }
      return true;  
    } 
  };  

  
}
