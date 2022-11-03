package org.vishia.gral.test.appl;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.test.GralPlotWindow;
import org.vishia.gral.widget.GralCanvasArea;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;

public class Show_Movie_SpeTelg {

  final GralMng gralMng = new GralMng(null);
  
  final GralPos pos = new GralPos(this.gralMng, "@screen,10..80,10..120");
  
  final GralWindow wind = new GralWindow(this.pos, "MainWindow", "SPE data transfer"
                        , GralWindow_ifc.windResizeable , this.gralMng);
  
  final GralCanvasArea plotArea = new GralCanvasArea(this.pos, "@Main,0..-10,0..0=canvas");
  
  final GralButton btnRunStop = new GralButton(this.pos, "@-8+3,-15..-2=runStop");
  { this.btnRunStop.setSwitchMode("run / stop?", "stop / run?");
  }
  
  final GralButton btnStep = new GralButton(this.pos, "@-4+3,-15..-2=step", "step", null);
  
  final GralCanvasStorage canvas = this.plotArea.getCanvasStore(0);
  
  GralColor colorLine = GralColor.getColor("bk");
  
  GralColor colorBlack = GralColor.getColor("bk");
  GralColor colorWhite = GralColor.getColor("wh");


  /**Dataset of the border of the data words in memory:
   * <pre>
   * +--+--+
   * |  |  |
   * +--+--+ </pre>
   * It is used for all Mem presentation (Master and Slave).
   */
  final GralCanvasStorage.FigureDataSet figData_Words = new GralCanvasStorage.FigureDataSet(); { 
    GralColor color = GralColor.getColor("bk");
    this.figData_Words.addPolyline(color, 1).point(0, 0).point(0,2);  // |
    for(int ix = 0; ix <20; ++ix) {
      this.figData_Words.addPolyline(color, 1)
      .point(ix,2)
      .point(ix+1,2)  // --+    20 times gives rectangles for each word.
      .point(ix+1,0)  //   |
      .point(ix,0);   // --+
    }
  }
  
  
  
  /**DataSet for the reference arrow
   * <pre>
   * ^
   * | </pre>
   */
  final GralCanvasStorage.FigureDataSet figData_RefTop = new GralCanvasStorage.FigureDataSet(); { 
    GralColor color = GralColor.getColor("drd");
    this.figData_RefTop.addPolyline(color, 1).point(0, 0).point(0, -1.8f);  //                |
    this.figData_RefTop.addPolyline(color, 1).point(-0.4f, -1).point(0, -2).point(0.4f, -1);  // ^
  }
  
  
  
  final GralCanvasStorage.FigureDataSet figData_serialRight = new GralCanvasStorage.FigureDataSet(); { 
    GralColor color = GralColor.getColor("bk");
    this.figData_serialRight.addPolyline(color, 1).point( 0, 0.3f).point(24, 0.3f);    // ==========
    this.figData_serialRight.addPolyline(color, 1).point( 0, -0.3f).point(24, -0.3f);  // 
    this.figData_serialRight.addPolyline(color, 1).point(23.0f, 0.5f).point(24.3f, 0.0f).point(23.0f, -0.5f);  // ==>
  }
  
  final GralCanvasStorage.FigureDataSet figData_serialRightSw = new GralCanvasStorage.FigureDataSet(); { 
    GralColor color = GralColor.getColor("bk");
    this.figData_serialRightSw.addPolyline(color, 1).point( 0, 0.3f).point(20, 0.3f);    // ==========
    this.figData_serialRightSw.addPolyline(color, 1).point( 0, -0.3f).point(20, -0.3f);  // 
    this.figData_serialRightSw.addArcline(color, 20f, 0, 0.3f, 0.3f, 0, 360);  // 
    this.figData_serialRightSw.addArcline(color, 22, 0, 0.3f, 0.3f, 0, 360);  // 
    this.figData_serialRightSw.addArcline(color, 21, -1.5f, 0.3f, 0.3f, 0, 360);  // 
    //this.figData_serialRightSw.addPolyline(color).point(21.0f, 0.5f).point(24.3f, 0.0f).point(23.0f, -0.5f);  // ==>
  }
  
  final GralCanvasStorage.FigureDataSet figData_TxSlaveSwitch = new GralCanvasStorage.FigureDataSet(); { 
    GralColor color = GralColor.getColor("bk");
    this.figData_TxSlaveSwitch.addPolyline(color, 5).point( 2, 0).point(0, 0).setVariantMask(0x1);  // 
    this.figData_TxSlaveSwitch.addPolyline(color, 5).point( 2, 0).point(1, -1.5f).setVariantMask(0x2);  // 
  }
  /**Contains only the fillin color.
   * 
   */
  GralCanvasStorage.Figure[] rxSlave1 = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] rxSlave2 = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] rxMaster = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] txSlave1 = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] txSlave2 = new GralCanvasStorage.Figure[20]; 
  
  
  float posSerialRef;
  
  /**For the current communication. Related to {@link #colorWordsMaster}.
   * If outside range, use white.
   */
  int ixColor;
  
  boolean bCont = true;

  /**Reference arrow in Ring-Master
   * 
   */
  GralCanvasStorage.Figure dmaRd1, dmaWr1, dmaRd2, dmaWr2, dmaRd3, dmaWr3;

  GralCanvasStorage.Figure txSlaveSwitch1, txSlaveSwitch2;
  
  /**Color field for the TxMaster word. */
  GralCanvasStorage.Figure serialOut1word;
  
  GralColor[] colorWordsMaster = new GralColor[20];
  
  GralColor colorWordUndef = GralColor.getColor("wh");
  
  public Show_Movie_SpeTelg() throws ParseException {
  }
  
  
  
  GralCanvasStorage.Figure wordsInStation ( ) {
    //GralCanvasStorage.Figure fig = this.plot.addFigure(this.pos, null, false);
    return null; //fig;
  }
  
  
  
  GralColor colorIx(int ixColor, int station) {
    GralColor[] colorArray = this.colorWordsMaster;
    if(ixColor <0 || ixColor >= colorArray.length) {
      return this.colorWhite;
    } else return colorArray[ixColor];
  }
  
  
  void init() throws ParseException {
    //
    this.pos.setPosition("10-2,10+1++");
    this.canvas.addFigure("dataWordsMaster", this.pos, this.figData_Words, false);
    this.pos.setPosition("10-2,40+1++");
    this.canvas.addFigure("dataWordsSlave1", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.rxSlave1[ix] = this.canvas.addFigure("rxSlave1-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    this.pos.setPosition("10-2,70+1++");
    this.canvas.addFigure("dataWordsSlave1", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.rxSlave2[ix] = this.canvas.addFigure("rxSlave1-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    //
    this.pos.setPosition("26-2,10+1++");
    this.canvas.addFigure("rxWordsMaster", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.txSlave1[ix] = this.canvas.addFigure("rxSlave1-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    this.pos.setPosition("26-2,40+1++");
    this.canvas.addFigure("txWordsSlave1", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.txSlave2[ix] = this.canvas.addFigure("rxSlave1-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    this.pos.setPosition("26-2,70+1++");
    this.canvas.addFigure("txWordsSlave1", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.rxMaster[ix] = this.canvas.addFigure("rxSlave1-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    //
    //create the figure for the read reference arrow:
    this.pos.setPosition("6-2, 10.5+1");                   // The ====> line for serial out and the ref arrow
    this.canvas.addFigure("serialOutMaster", this.pos, this.figData_serialRight, false);
    this.dmaRd1 = this.canvas.addFigure("refData_Master", this.pos, this.figData_RefTop, true);
    this.pos.setPosition("4+2, 40.5+1");
    this.canvas.addFigure("serialOutMaster", this.pos, this.figData_serialRightSw, false);
    this.dmaRd2 = this.canvas.addFigure("refData_Master", this.pos, this.figData_RefTop, true);
    this.pos.setPosition("4+2, 60.5+1");
    this.txSlaveSwitch1 = this.canvas.addFigure("txSlave1Switch", this.pos, this.figData_TxSlaveSwitch, true);
    this.pos.setPosition("4+2, 70.5+1");
    this.canvas.addFigure("serialOutMaster", this.pos, this.figData_serialRightSw, false);
    this.dmaRd3 = this.canvas.addFigure("refData_Master", this.pos, this.figData_RefTop, true);
    this.pos.setPosition("4+2, 90.5+1");
    this.txSlaveSwitch2 = this.canvas.addFigure("txSlave2Switch", this.pos, this.figData_TxSlaveSwitch, true);
    //
    this.pos.setPosition("30-2, 10.5+1");                   // The ====> line for serial out and the ref arrow
    this.canvas.addFigure("serialOutMaster", this.pos, this.figData_serialRight, false);
    this.dmaWr1 = this.canvas.addFigure("refData_Master", this.pos, this.figData_RefTop, true);
    this.pos.setPosition("30-2, 40.5+1");
    this.canvas.addFigure("serialOutMaster", this.pos, this.figData_serialRight, false);
    this.dmaWr2 = this.canvas.addFigure("refData_Master", this.pos, this.figData_RefTop, true);
    this.pos.setPosition("30-2, 70.5+1");
    this.canvas.addFigure("serialOutMaster", this.pos, this.figData_serialRight, false);
    this.dmaWr3 = this.canvas.addFigure("refData_Master", this.pos, this.figData_RefTop, true);
    //

    

    boolean bTest1 = true;
    if(bTest1) {
    //------------------------------------------ // 20 colors for Master word
    for(int ix = 0; ix <20; ++ix) {             
      int red = (ix%2==1) ? 0xc0: 0x80;
      GralColor colorFill = GralColor.getColor(red, (19-ix)*10 + 65, ix*10 + 65);
      this.colorWordsMaster[ix] = colorFill;
    }
    //---------------------------------------------------- //Fillin of the master tx words 
    this.pos.setPosition("10-2, 10+1++");
    for(int ix = 0; ix <20; ++ix) {                       
      this.canvas.addFigure("wordTxMaster", this.pos, new GralCanvasStorage.Fillin("X", this.colorWordsMaster[ix]), false);
      //this.plot.drawFillin(this.pos, this.colorWordsMaster[ix]);
      this.pos.setNextPosition();
    }
    //GralPlotArea.UserUnits units = this.plot.userUnitsPerGrid(0.0f,  0.0f,  1.0f,  1.0f);
    //
    this.pos.setPosition("13-2, 35+1");                    // value on the serial plug
    GralColor colorFillin = this.colorWordsMaster[8];
    this.serialOut1word = this.canvas.addFigure("wordTxMaster", this.pos, new GralCanvasStorage.Fillin("X", colorFillin), true);
    //
    }
    LogMessage log = new LogMessageStream(System.out);
    this.gralMng.createGraphic("SWT", 'E', log);
    
    this.posSerialRef = 24.0f;
    
    this.plotArea.redraw(100, 100);
  }
  
  
  
  /**Step to show moving in the image. Called any 100 ms.
   * @throws ParseException
   */
  void step() throws ParseException {
    //this.ref1.move(5, 0);                                  // move 0.5 grid to right
    if(  this.btnRunStop.getState() == GralButton.State.On
      || this.btnStep.wasPressed()) {
      if((this.posSerialRef +=1.0) >=23) { 
        if(this.posSerialRef >=25) {                         // waits a while without paint 
          this.posSerialRef = 0;                             // then starts from 0 again
          this.ixColor = 0;
          this.dmaRd1.setNewPosition("6-2, 10.5+1");         // start position, first increment is done before usage
          for(GralCanvasStorage.Figure rx1 : this.rxSlave1 ) {
            rx1.data.color = this.colorWhite;
          }
          this.plotArea.repaint();
          Debugutil.stop();
        }
      }
      else {                                               // shows the output.
        if(ixColor >=1 && ixColor < 21) {
          this.rxSlave1[ixColor-1].data.color = this.serialOut1word.data.color;  // move content from rx Port to rx data
        }
        GralColor colorWord;
        if(this.ixColor >=0 && ixColor <20) {
          colorWord = this.colorWordsMaster[this.ixColor];
        } else if(this.ixColor >=21) {
          colorWord = this.colorWhite;
        } else {
          colorWord = this.serialOut1word.data.color;      // last step remain the color.
        }
        this.serialOut1word.data.color = colorWord; 
        //this.dmaRd1.setNewPosition(GralPos.refer, GralPos.samesize, GralPos.refer + 1.0f, GralPos.samesize);
        this.dmaRd1.bShow = this.ixColor>=0 && this.ixColor <20;
        this.dmaRd1.setNewPosition(4, 6, 10.5f+ this.ixColor, GralPos.size +1);
        this.dmaRd2.bShow = this.ixColor>=1 && this.ixColor <21;
        this.dmaRd2.setNewPosition(4, 6, 40.5f+ this.ixColor-1.0f, GralPos.size +1);
        this.txSlaveSwitch1.setVariant(0);
        this.txSlaveSwitch2.setVariant(1);
        this.ixColor +=1;
        this.plotArea.redraw(20, 100, true);
        Debugutil.stop();
      }
    }
  }
  
  
  
  
  void exec() throws ParseException {
      init();
      while(this.gralMng.isRunning()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {}
        step();
      }
  }
  
  public static void main(String[] args) {
    try {
      Show_Movie_SpeTelg thiz = new Show_Movie_SpeTelg();    
      thiz.exec();
    } catch(Exception exc) { 
      System.err.println(exc.getMessage());
      exc.printStackTrace(System.err);
      throw new RuntimeException("unexpected", exc);
    }
  
  }
  
}
