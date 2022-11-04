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
  
  final GralButton btnStepBack = new GralButton(this.pos, "@-4+3,-30..-17=back", "back", null);
  
  final GralCanvasStorage canvas = this.plotArea.getCanvasStore(0);
  
  GralColor colorLine = GralColor.getColor("bk");
  
  GralColor colorBlack = GralColor.getColor("bk");
  GralColor colorWhite = GralColor.getColor("wh");


  /**Dataset of the border of the data words in memory:
   * <pre>
   * +--+
   * |  |
   * +--+ </pre>
   * It is used for all Mem presentation (Master and Slave).
   */
  final GralCanvasStorage.FigureDataSet figData_Word = new GralCanvasStorage.FigureDataSet(); { 
    GralColor color = GralColor.getColor("bk");
    this.figData_Word.addPolyline(color, 1).point(0, 0).point(0,2)
                      .point(1, 2).point(1,0).point(0,0);  // |
  }
  
  
  
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
  final GralCanvasStorage.FigureDataSet figData_RefTop = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      GralColor color = GralColor.getColor("drd");
      setSpread(0,0,1,2);
      addPolyline(color, 1).point(0.5f, 1.9f).point(0.5f, 0.1f);  //                |
      addPolyline(color, 1).point(0.1f, 1).point(0.5f, 0).point(0.9f, 1);  // ^
      
    }
  };
  
//  { 
//    GralColor color = GralColor.getColor("drd");
//    this.figData_RefTop.setSpread(0,0,1,2);
//    this.figData_RefTop.addPolyline(color, 1).point(0.5f, 1.9f).point(0.5f, 0.1f);  //                |
//    this.figData_RefTop.addPolyline(color, 1).point(0.1f, 1).point(0.5f, 0).point(0.9f, 1);  // ^
//  }
  
  
  
  final GralCanvasStorage.FigureDataSet figData_serialRight = new GralCanvasStorage.FigureDataSet() { 
    @Override public void init() {
      GralColor color = GralColor.getColor("bk");
      addPolyline(color, 1).point( 0, 0.3f).point(23.2f, 0.3f);    // ==========
      addPolyline(color, 1).point( 0, -0.3f).point(23.2f, -0.3f);  // 
      addPolyline(color, 1).point(22.7f, 0.5f).point(24.0f, 0.0f).point(22.7f, -0.5f);  // ==>
      }
  };
  
  final GralCanvasStorage.FigureDataSet figData_serialRightSwitch = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      GralColor color = GralColor.getColor("bk");
      addPolyline(color, 1).point( 0, 0.3f).point(24, 0.3f);    // ==========
      addPolyline(color, 1).point( 0, -0.3f).point(24, -0.3f);  // 
      addPolyline(color, 1).point( 26.3f, 0.3f).point(27, 0.3f);    //        =
      addPolyline(color, 1).point( 26.3f, -0.3f).point(27, -0.3f); // ==========
      addPolyline(color, 1).point(26.7f, 0.5f).point(28.0f, 0.0f).point(26.7f, -0.5f);  // ==>
      // circle for the switch
      addArcline(color, 24f, 0, 0.3f, 0.3f, 0, 360);            //   o
      addArcline(color, 26, 0, 0.3f, 0.3f, 0, 360);             //      o
      addArcline(color, 25, -1.5f, 0.3f, 0.3f, 0, 360);         //    o 
    }
  };
  
  final GralCanvasStorage.FigureDataSet figData_TxSlaveSwitch = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      setSpread(0,0,2.5f,2.5f);
      GralColor color = GralColor.getColor("bk");
      addPolyline(color, 5).point( 1.9f, 1.9f).point(0.1f, 1.9f).setVariantMask(0x1);  // 
      addPolyline(color, 5).point( 1.9f, 1.9f).point(1.0f, 0.5f).setVariantMask(0x2);  // 
  } };
  /**Contains only the fillin color.
   * 
   */
  GralCanvasStorage.Figure[] rxDataSlave1 = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] rxDataSlave2 = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] rxDataMaster = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] txDataSlave1 = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] txDataSlave2 = new GralCanvasStorage.Figure[20]; 
  
  
  /**For the current communication. Related to {@link #colorWordsMaster}.
   * If outside range, use white.
   */
  int ixTime;
  
  boolean bCont = true;

  /**Reference arrow in Ring-Master
   * 
   */
  GralCanvasStorage.Figure dmaRd1, dmaWr1, dmaRd2, dmaWr2, dmaRd3, dmaWr3;

  GralCanvasStorage.Figure txSlaveSwitch1, txSlaveSwitch2;
  
  /**Color field for the TxMaster word. */
  GralCanvasStorage.Figure serialOutMaster1, serialOutSlave1, serialOutSlave2;
  
  GralColor[] colorWordsMaster = new GralColor[20];
  
  GralColor[] colorWordsRdSlave1 = new GralColor[20];
  
  GralColor[] colorWordsRdSlave2 = new GralColor[20];
  
  int[] txSlave1SwitchVariant = { 0,0,0,0,1,1,1,0,0,0,0,0,0,0,1,1,1,0,0,0  ,0,0,0,0,0,0,0,0 };
  int[] txSlave2SwitchVariant = { 0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,1,1  ,0,0,0,0,0,0,0,0 };
  
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
    this.figData_serialRight.init();
    this.figData_serialRightSwitch.init();
    this.figData_RefTop.init();
    this.figData_TxSlaveSwitch.init();
    this.pos.setPosition("10-2,10+1++");
    this.canvas.addFigure("dataWordsMaster", this.pos, this.figData_Words, false);
    this.pos.setPosition("10-2,40+1++");
    this.canvas.addFigure("dataWordsSlave1", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.rxDataSlave1[ix] = this.canvas.addFigure("rxDataSlave1-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    this.pos.setPosition("10-2,70+1++");
    this.canvas.addFigure("dataWordsSlave2", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.rxDataSlave2[ix] = this.canvas.addFigure("rxDataSlave2-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    //
    this.pos.setPosition("26-2,10+1++");
    this.canvas.addFigure("txWordsSlave2", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.rxDataMaster[ix] = this.canvas.addFigure("rxDataMaster-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    this.pos.setPosition("26-2,40+1++");
    this.canvas.addFigure("rxWordsMaster", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.txDataSlave1[ix] = this.canvas.addFigure("txDataSlave1-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    this.pos.setPosition("26-2,70+1++");
    this.canvas.addFigure("txWordsSlave1", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.txDataSlave2[ix] = this.canvas.addFigure("txDataSlave2-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    //
    //create the figure for the read reference arrow:
    this.pos.setPosition("6-2, 10.5+1");                   // The ====> line for serial out and the ref arrow
    this.canvas.addFigure("serialOutMaster", this.pos, this.figData_serialRight, false);
    this.pos.setPosition("8-2, 10+1");                     // reference arrow
    this.dmaRd1 = this.canvas.addFigure("refData_Master", this.pos, this.figData_RefTop, true);
    this.dmaRd1.bShow = false;
    this.pos.setPosition("4+2, 36.5+1");
    this.canvas.addFigure("serialOutSlave1Switch", this.pos, this.figData_serialRightSwitch, false);
    this.pos.setPosition("8-2, 40+1");                     // reference arrow
    this.dmaRd2 = this.canvas.addFigure("refData_Slave1", this.pos, this.figData_RefTop, true);
    this.dmaRd2.bShow = false;
    this.pos.setPosition("6+2, 60.5+1");
    this.txSlaveSwitch1 = this.canvas.addFigure("txSlave1Switch", this.pos, this.figData_TxSlaveSwitch, true);
    this.pos.setPosition("4+2, 66.5+1");
    this.canvas.addFigure("serialOutSlave2Switch", this.pos, this.figData_serialRightSwitch, false);
    this.pos.setPosition("8-2, 70+1");                     // reference arrow
    this.dmaRd3 = this.canvas.addFigure("refData_Slave2", this.pos, this.figData_RefTop, true);
    this.pos.setPosition("8-2, 90.5+1");
    this.txSlaveSwitch2 = this.canvas.addFigure("txSlave2Switch", this.pos, this.figData_TxSlaveSwitch, true);
    //
    this.pos.setPosition("6+0, 0+100");
    GralCanvasStorage.FigureDataSet lines_rxSlaveBackMaster = new GralCanvasStorage.FigureDataSet();
    lines_rxSlaveBackMaster.addPolyline(this.colorBlack, 1).point(96.5f, -0.2f).dx(1.6f).dy(4.4f)
    .point(3.8f, 4.2f).dy(-4.4f).dx(4.0f).dy(-22.2f).dx(22.2f);    
    lines_rxSlaveBackMaster.addPolyline(this.colorBlack, 1).point(96.5f, 0.2f).dx(1.2f).dy(3.6f)
    .point(4.2f, 3.8f).dy(-3.6f).dx(4.0f).dy(-22.2f).dx(21.8f);    
    this.canvas.addFigure("rxSlaveBackMaster", this.pos, lines_rxSlaveBackMaster, false);
    
    this.pos.setPosition("28-2, 10+1");                     // reference arrow
    this.dmaWr1 = this.canvas.addFigure("refData_RdMaster", this.pos, this.figData_RefTop, true);
    this.pos.setPosition("30-2, 40.5+1");
    this.dmaWr2 = this.canvas.addFigure("refData_RdSlave1", this.pos, this.figData_RefTop, true);
    this.pos.setPosition("28-2, 70+1");                     // reference arrow
    this.dmaWr3 = this.canvas.addFigure("refData_RdSlave2", this.pos, this.figData_RefTop, true);
    //

    

    boolean bTest1 = true;
    if(bTest1) {
    //------------------------------------------ // 20 colors for Master word
    for(int ix = 0; ix <20; ++ix) {             
      int red = (ix%2==1) ? 0xc0: 0x80;
      GralColor colorFill = GralColor.getColor(red, (19-ix)*10 + 65, ix*10 + 65);
      this.colorWordsMaster[ix] = colorFill;
    }
    this.colorWordsRdSlave1[4] = GralColor.getColor("lrd");
    this.colorWordsRdSlave1[5] = GralColor.getColor("rd");
    this.colorWordsRdSlave1[6] = GralColor.getColor("or");
    //---------------------------------------------------- //Fillin of the master tx words 
    this.pos.setPosition("10-2, 10+1++");
    for(int ix = 0; ix <20; ++ix) {                       
      this.canvas.addFigure("wordsTxMaster-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWordsMaster[ix]), false);
      //this.plot.drawFillin(this.pos, this.colorWordsMaster[ix]);
      this.pos.setNextPosition();
    }
    //GralPlotArea.UserUnits units = this.plot.userUnitsPerGrid(0.0f,  0.0f,  1.0f,  1.0f);
    //
    this.pos.setPosition("7-2, 35+1");                    // value on the serial plug
    this.canvas.addFigure("serialOutMasterRect", this.pos, this.figData_Word, false);
    GralColor colorFillin = this.colorWhite;
    this.serialOutMaster1 = this.canvas.addFigure("serialOutMaster1", this.pos, new GralCanvasStorage.Fillin("X", colorFillin), true);
    //
    this.pos.setPosition("7-2, 65+1");                    // value on the serial plug
    this.canvas.addFigure("serialOutSlave1Rect", this.pos, this.figData_Word, false);
    this.serialOutSlave1 = this.canvas.addFigure("serialOutSlave1", this.pos, new GralCanvasStorage.Fillin("X", colorFillin), true);
    //
    this.pos.setPosition("7-2, 95+1");                    // value on the serial plug
    this.canvas.addFigure("serialOutSlave2Rect", this.pos, this.figData_Word, false);
    this.serialOutSlave2 = this.canvas.addFigure("serialOutSlave2", this.pos, new GralCanvasStorage.Fillin("X", colorFillin), true);
    //
    }
    LogMessage log = new LogMessageStream(System.out);
    this.gralMng.createGraphic("SWT", 'E', log);
    
    this.plotArea.redraw(100, 100);
  }
  
  
  
  /**Step to show moving in the image. Called any 100 ms.
   * @throws ParseException
   */
  void step() throws ParseException {
    //this.ref1.move(5, 0);                                  // move 0.5 grid to right
    boolean stepBack = this.btnStepBack.wasPressed();
    if(  this.btnRunStop.getState() == GralButton.State.On
      || this.btnStep.wasPressed() || stepBack) {
      if(stepBack) { if(--this.ixTime <0) { ixTime = 25;} }
      else { this.ixTime +=1; }
      if(ixTime >=25) { 
        if(ixTime >=27) {                         // waits a while without paint 
          this.ixTime = 0;
          this.dmaRd1.setNewPosition("8-2, 10+1");         // start position, first increment is done before usage
          for(GralCanvasStorage.Figure rx1 : this.txDataSlave1 ) {
            rx1.data.color = this.colorWhite;
          }
          for(GralCanvasStorage.Figure rx1 : this.txDataSlave2 ) {
            rx1.data.color = this.colorWhite;
          }
          for(GralCanvasStorage.Figure rx1 : this.rxDataSlave1 ) {
            rx1.data.color = this.colorWhite;
          }
          for(GralCanvasStorage.Figure rx1 : this.rxDataSlave2 ) {
            rx1.data.color = this.colorWhite;
          }
          for(GralCanvasStorage.Figure rx1 : this.rxDataMaster ) {
            rx1.data.color = this.colorWhite;
          }
        }
      }
      else {                                               // shows the output.
        if(this.ixTime >=2 && this.ixTime < 20 && this.colorWordsRdSlave1[this.ixTime-2] !=null) {
          this.txDataSlave1[ixTime].data.color = this.colorWordsRdSlave1[this.ixTime-2];
        }
        if(this.ixTime >=3 && this.ixTime < 23) {
          this.rxDataMaster[this.ixTime-3].data.color = this.serialOutSlave2.data.color;  // move content from rx Port to rx data
        }
        if(this.ixTime >=2 && this.ixTime < 22) {
          GralColor colorRx = this.rxDataSlave2[this.ixTime-2].data.color = this.serialOutSlave1.data.color;  // move content from rx Port to rx data
          this.serialOutSlave2.data.color = colorRx;
        }
        if(this.ixTime >=1 && this.ixTime < 21) {
          GralColor colorRx = this.rxDataSlave1[this.ixTime-1].data.color = this.serialOutMaster1.data.color;  // move content from rx Port to rx data
          this.serialOutSlave1.data.color = colorRx;
        }
        GralColor colorWord;
        if(this.ixTime >=0 && this.ixTime <20) {
          colorWord = this.colorWordsMaster[this.ixTime];
        } else if(this.ixTime >=21) {
          colorWord = this.colorWhite;
        } else {
          colorWord = this.serialOutMaster1.data.color;      // last step remain the color.
        }
        this.serialOutMaster1.data.color = colorWord; 
        //this.dmaRd1.setNewPosition(GralPos.refer, GralPos.samesize, GralPos.refer + 1.0f, GralPos.samesize);
        this.dmaRd1.bShow = this.ixTime>=0 && this.ixTime <20;
        this.dmaRd1.setNewPosition(6, 8, 10.0f+ this.ixTime, GralPos.size +1);
        this.dmaRd2.bShow = this.ixTime>=1 && this.ixTime <21;
        this.dmaRd2.setNewPosition(6, 8, 40.0f+ this.ixTime-1.0f, GralPos.size +1);
        this.dmaRd3.bShow = this.ixTime>=2 && this.ixTime <22;
        this.dmaRd3.setNewPosition(6, 8, 70.0f+ this.ixTime-2.0f, GralPos.size +1);
        this.txSlaveSwitch1.setVariant(this.txSlave1SwitchVariant[ixTime]);
        this.txSlaveSwitch2.setVariant(this.txSlave2SwitchVariant[ixTime]);
      }
      this.plotArea.redraw(20, 100, true);
      Debugutil.stop();
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
