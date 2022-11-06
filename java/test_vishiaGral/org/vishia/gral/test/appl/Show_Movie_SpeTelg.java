package org.vishia.gral.test.appl;

import java.text.ParseException;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralWindow_ifc;
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
  final GralButton btnSlow = new GralButton(this.pos, "@-8+3,-30..-17=slow");
  { this.btnSlow.setSwitchMode("fast / slow?", "slow / fast?");
  }
  
  final GralButton btnRefresh = new GralButton(this.pos, "@-8+3,-40..-31=refresh", "refresh", null);
  
  
  final GralButton btnDebug = new GralButton(this.pos, "@-4+3,-40..-31=debug", "debug", null);
  
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
  final GralCanvasStorage.FigureDataSet figData_RefDn = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      GralColor color = GralColor.getColor("drd");
      setSpread(0,0,1,2);
      addPolyline(color, 1).point(0.5f, 1.9f).point(0.5f, 0.2f);  //                |
      addPolyline(color, 1).point(0.1f, 1).point(0.5f, 0.1f).point(0.9f, 1);  // ^
      
    }
  };
  
  /**DataSet for the reference arrow
   * <pre>
   * ^
   * | </pre>
   */
  final GralCanvasStorage.FigureDataSet figData_RefUp = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      GralColor color = GralColor.getColor("drd");
      setSpread(0,0,1,2f);
      addPolyline(color, 1).point(0.5f, 1.8f).point(0.5f, 0.1f);  //                |
      addPolyline(color, 1).point(0.1f, 1).point(0.5f, 1.8f).point(0.9f, 1);  // ^
      
    }
  };
  
  final GralCanvasStorage.FigureDataSet figData_calc = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      GralColor color = GralColor.getColor("drd");
      float x0 = 5.5f; float y0 = 7f;
      setSpread(x0,y0-0.4f, 12,13);
      addPolyline(color, 1).point(x0, y0).dxyg(14.5f, 55);  //                |
      GralPoint lastPoint = 
      addPolyline(color, 1).point(x0 + 0.5f, y0 - 0.4f).dxyg(14.5f, 55).lastPoint();  //                |
      addPolyline(color, 1).point(lastPoint.x - 0.9f, lastPoint.y + 0.4f).dxyg(1, 15).dxyg(-1, 90);  // ^
      
    }
  };
  

  final GralCanvasStorage.FigureDataSet figData_getMeas = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      GralColor color = GralColor.getColor("drd");
      setSpread(29.5f, 2.0f, 35, 8);
      GralPoint lastPoint = addPolyline(color, 3).point(29.5f, 2.0f).dxy(3, 7.5f).lastPoint();  //                |
      addPolyline(color, 3).point(lastPoint.x - 1.3f, lastPoint.y - 1.2f).dxy(1.3f, +1.2f).dxy(0f, -1.6f);  // ^
      lastPoint = addPolyline(color, 3).point(29.5f, 2.0f).dxy(30, 3.5f).dxy(4, 4).lastPoint();  //                |
      addPolyline(color, 3).point(lastPoint.x - 1.5f, lastPoint.y - 0.5f).dxy(1.5f, +0.5f).dxy(-0.2f, -1.2f);  // ^
    }
  };
  
  final GralCanvasStorage.FigureDataSet figData_prcsetOutput = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      GralColor color = GralColor.getColor("drd");
      setSpread(-5.5f, 0f, 6, 26);
      GralPoint lastPoint = addPolyline(color, 3).point(-5.5f, 26.0f).dxy(5.5f, -24f).lastPoint();  //                |
      addPolyline(color, 3).point(lastPoint.x - 0.8f, lastPoint.y + 1.0f).dxy(0.8f, -1.0f).dxy(0.3f, +1.0f);  // ^
    }
  };
  

  
  final GralCanvasStorage.FigureDataSet figData_stationBox = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      GralColor color = GralColor.getColor("drd");
      addPolyline(color, 1).point(0, 0).dx(26.5f).dy(26).dx(-26.5f).dy(-26);  // ^
      
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
      addPolyline(color, 1).point( 0, 0.2f).point(21.2f, 0.2f);    // ==========
      addPolyline(color, 1).point( 0, -0.2f).point(21.2f, -0.2f);  // 
      addPolyline(color, 1).point(20.7f, 0.5f).point(22.0f, 0.0f).point(20.7f, -0.5f);  // ==>
      }
  };
  
  final GralCanvasStorage.FigureDataSet figData_serialRightSwitch = new GralCanvasStorage.FigureDataSet() {
    @Override public void init() {
      GralColor color = GralColor.getColor("bk");
      addPolyline(color, 1).point( 0, 0.2f).point(24, 0.2f);    // ==========
      addPolyline(color, 1).point( 0, -0.2f).point(24, -0.2f);  // 
      addPolyline(color, 1).point( 26.3f, 0.2f).point(27, 0.2f);    //        =
      addPolyline(color, 1).point( 26.3f, -0.2f).point(27, -0.2f); // ==========
      addPolyline(color, 1).point(26.7f, 0.5f).point(28.0f, 0.0f).point(26.7f, -0.5f);  // ==>
      addPolyline(color, 1).point(24.8f,-1.7f).dy(-20.0f).dx(-21.0f);
      addPolyline(color, 1).point(25.2f,-1.7f).dy(-20.4f).dx(-21.4f);
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
  GralCanvasStorage.Figure[] txDataMaster = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] rxDataSlave1 = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] rxDataSlave2 = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] rxDataMaster = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] txDataSlave1 = new GralCanvasStorage.Figure[20]; 
  GralCanvasStorage.Figure[] txDataSlave2 = new GralCanvasStorage.Figure[20]; 
  
  
  /**For the current communication. Related to {@link #colorWordsMaster}.
   * If outside range, use white.
   */
  int ixTime;
  
  int ixColorValueCtrl;
  
  boolean bCont = true;

  /**Reference arrow in Ring-Master
   * 
   */
  GralCanvasStorage.Figure dmaRdMaster, dmaWrMaster, dmaWrSlave1, dmaRdSlave1, dmaWrSlave2, dmaRdSlave2;

  GralCanvasStorage.Figure txSlaveSwitch1, txSlaveSwitch2;
  
  /**Color field for the TxMaster word. */
  GralCanvasStorage.Figure serialOutMaster1, serialOutSlave1, serialOutSlave2;
  
  GralColor[] colorWordsMaster = new GralColor[20];
  
  GralColor[] colorWordsRdSlave1 = new GralColor[20];
  
  GralColor[] colorWordsRdSlave2 = new GralColor[20];
  
  
  GralColor[] colorValueCtrl = new GralColor[7];
  
  //int[] txSlave1SwitchVariant = { 0,0,0,0,1,1,1,0,0,0,0,0,0,0,1,1,1,0,0,0  ,0,0,0,0,0,0,0,0 };
  //int[] txSlave2SwitchVariant = { 0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,1,1  ,0,0,0,0,0,0,0,0 };
  
  /**It is an arrow to show calculation occurrence. */
  GralCanvasStorage.Figure calcMaster1;

  GralCanvasStorage.Figure prcgetMeas, prcsetOutput;
  
  /**Fillin of data for measurement and output. */
  GralCanvasStorage.Figure dataEnvMeas, dataEnvOutput;
  
  GralCanvasStorage.Figure[] timeRef = new GralCanvasStorage.Figure[3];
  
  
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
    for(int ix = 0; ix < 7; ++ix) {
      int gn = 80*(ix & 1)* 255 -80;  //toogling with odd/even
      this.colorValueCtrl[ix] = GralColor.getColor(255-140 + 20 * ix, gn, 255 - 20 * ix);
    }
    this.figData_serialRight.init();
    this.figData_serialRightSwitch.init();
    this.figData_RefDn.init();
    this.figData_RefUp.init();
    this.figData_TxSlaveSwitch.init();
    this.figData_calc.init();
    this.figData_stationBox.init();
    this.pos.setPosition("10-2,12+1++");
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
    this.pos.setPosition("26-2,12+1++");
    this.canvas.addFigure("rxDataMaster", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.rxDataMaster[ix] = this.canvas.addFigure("rxDataMaster-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    this.pos.setPosition("26-2,40+1++");
    this.canvas.addFigure("txDataSlave1", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.txDataSlave1[ix] = this.canvas.addFigure("txDataSlave1-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    this.pos.setPosition("26-2,70+1++");
    this.canvas.addFigure("txDataSlave2", this.pos, this.figData_Words, false);
    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.txDataSlave2[ix] = this.canvas.addFigure("txDataSlave2-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWhite), true);
      this.pos.setPosition(",+1");
    }
    //
    //create the figure for the read reference arrow:
    this.pos.setPosition("6-2, 12.5+1");                   // The ====> line for serial out and the ref arrow
    this.canvas.addFigure("serialOutMaster", this.pos, this.figData_serialRight, false);
    this.pos.setPosition("8-2, 12+1");                     // reference arrow
    this.dmaRdMaster = this.canvas.addFigure("refData_RdMaster", this.pos, this.figData_RefUp, true);
    this.dmaRdMaster.bShow = false;
    this.pos.setPosition("4+2, 36.5+1");
    this.canvas.addFigure("serialOutSlave1Switch", this.pos, this.figData_serialRightSwitch, false);
    this.pos.setPosition("8-2, 40+1");                     // reference arrow
    this.dmaWrSlave1 = this.canvas.addFigure("refData_WrSlave1", this.pos, this.figData_RefDn, true);
    this.dmaWrSlave1.bShow = false;
    this.pos.setPosition("6+2, 60.5+1");
    this.txSlaveSwitch1 = this.canvas.addFigure("txSlave1Switch", this.pos, this.figData_TxSlaveSwitch, true);
    this.pos.setPosition("4+2, 66.5+1");
    this.canvas.addFigure("serialOutSlave2Switch", this.pos, this.figData_serialRightSwitch, false);
    this.pos.setPosition("8-2, 70+1");                     // reference arrow
    this.dmaWrSlave2 = this.canvas.addFigure("refData_WrSlave2", this.pos, this.figData_RefDn, true);
    this.dmaWrSlave2.bShow = false;
    this.pos.setPosition("8-2, 90.5+1");
    this.txSlaveSwitch2 = this.canvas.addFigure("txSlave2Switch", this.pos, this.figData_TxSlaveSwitch, true);
    //
    this.pos.setPosition("6+0, 0+100");
    GralCanvasStorage.FigureDataSet lines_rxSlaveBackMaster = new GralCanvasStorage.FigureDataSet();
    lines_rxSlaveBackMaster.addPolyline(this.colorBlack, 1).point(96.5f, -0.2f).dx(1.6f).dy(4.4f)
    .point(3.8f, 4.2f).dy(-4.4f).dx(6.0f).dy(-22.2f).dx(22.0f);    
    lines_rxSlaveBackMaster.addPolyline(this.colorBlack, 1).point(96.5f, 0.2f).dx(1.2f).dy(3.6f)
    .point(4.2f, 3.8f).dy(-3.6f).dx(6.0f).dy(-22.2f).dx(21.6f);    
    this.canvas.addFigure("rxSlaveBackMaster", this.pos, lines_rxSlaveBackMaster, false);
    
    this.pos.setPosition("28-2, 12+1");                     // reference arrow
    this.dmaWrMaster = this.canvas.addFigure("refData_WrMaster", this.pos, this.figData_RefUp, true);
    this.dmaWrMaster.bShow = false;
    this.pos.setPosition("28-2, 40.5+1");
    this.dmaRdSlave1 = this.canvas.addFigure("refData_RdSlave1", this.pos, this.figData_RefDn, true);
    this.dmaRdSlave1.bShow = false;
    this.pos.setPosition("28-2, 70+1");                     // reference arrow
    this.dmaRdSlave2 = this.canvas.addFigure("refData_RdSlave2", this.pos, this.figData_RefDn, true);
    this.dmaRdSlave2.bShow = false;
    //

    

    boolean bTest1 = true;
    if(bTest1) {
    //------------------------------------------ // 20 colors for Master word
    for(int ix = 0; ix <20; ++ix) {             
      int red = (ix%2==1) ? 0xc0: 0x80;
      GralColor colorFill = GralColor.getColor(red, (19-ix)*10 + 65, ix*10 + 65);
      this.colorWordsMaster[ix] = colorFill;
    }
//    this.colorWordsRdSlave1[0] = GralColor.getColor(0xff8080);
//    this.colorWordsRdSlave1[1] = GralColor.getColor(0xc020a0);
    this.colorWordsRdSlave1[4] = GralColor.getColor("lrd");
    this.colorWordsRdSlave1[5] = GralColor.getColor("rd");
    this.colorWordsRdSlave1[6] = GralColor.getColor("or");
    this.colorWordsRdSlave2[3] = GralColor.getColor("bl");
    this.colorWordsRdSlave2[6] = GralColor.getColor("lbl");
    this.colorWordsRdSlave2[15] = GralColor.getColor("lbl");
    this.colorWordsRdSlave2[16] = GralColor.getColor("bl");
    this.colorWordsRdSlave2[17] = GralColor.getColor("pbl");
    //---------------------------------------------------- //Fillin of the master tx words 
    this.pos.setPosition("10-2, 12+1++");
    for(int ix = 0; ix <20; ++ix) {                       
      boolean bDynamic = (ix==12);                         // color changed from calculation
      this.txDataMaster[ix] = this.canvas.addFigure("txDataMaster-" + ix, this.pos, new GralCanvasStorage.Fillin("X", this.colorWordsMaster[ix]), bDynamic);
      //this.plot.drawFillin(this.pos, this.colorWordsMaster[ix]);
      this.pos.setNextPosition();
    }
    //GralPlotArea.UserUnits units = this.plot.userUnitsPerGrid(0.0f,  0.0f,  1.0f,  1.0f);
    //
    this.pos.setPosition("7-2, 35+1");                    // value on the serial plug
    this.canvas.addFigure("serialOutMasterRect", this.pos, this.figData_Word, false);
    GralColor colorFillin = GralColor.getColor("lma");
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
    this.pos.setPosition("6..30, 9..32");
    this.canvas.addFigure("boxMaster1", this.pos, this.figData_stationBox, false);
    this.calcMaster1 = this.canvas.addFigure("calcMaster1", this.pos, this.figData_calc, true);
    //
    this.pos.setPosition("6..30, 39..72");
    this.canvas.addFigure("boxMaster2", this.pos, this.figData_stationBox, false);
    //
    this.pos.setPosition("6..30, 69..102");
    this.canvas.addFigure("boxMaster3", this.pos, this.figData_stationBox, false);
    //
    this.pos.setPosition("36-2, 8+30");
    this.dataEnvMeas = this.canvas.addFigure("dataEnvMeas", this.pos, new GralCanvasStorage.Fillin("X", this.colorValueCtrl[0]) , true);
    this.prcgetMeas = this.canvas.addFigure("prcgetMeas", this.pos, this.figData_getMeas , true);
    this.prcgetMeas.bShow = false; 
    this.pos.setPosition(",+50");
    this.prcsetOutput = this.canvas.addFigure("prcsetOutput", this.pos, this.figData_prcsetOutput , true).show(false);
    this.dataEnvOutput = this.canvas.addFigure("dataEnvOutput", this.pos, new GralCanvasStorage.Fillin("X", this.colorValueCtrl[6]) , true);
    for(int ix = 0; ix < this.timeRef.length; ++ix) {
      this.pos.setPosition(this.pos, 38, GralPos.size -2, 8 + 30*ix, GralPos.size + 1);
      this.timeRef[ix] = this.canvas.addFigure("timeRef-" + ix, this.pos, this.figData_RefUp , true);
    }
    
    } //bTest
    LogMessage log = new LogMessageStream(System.out);
    this.gralMng.createGraphic("SWT", 'E', log);
    
    this.plotArea.redraw(100, 100);
  }
  
  
  
  /**Step to show moving in the image. Called any 100 ms.
   * @throws ParseException
   */
  void step() throws ParseException {
    boolean dbg = this.btnDebug.wasPressed();
    boolean stepBack = this.btnStepBack.wasPressed();
    if(this.btnRefresh.wasPressed()) {
      this.plotArea.redraw(20, 20, false);
    }
    else if(  this.btnRunStop.getState() == GralButton.State.On
      || this.btnStep.wasPressed() || stepBack || dbg) {
      if(stepBack) { if(--this.ixTime <0) { ixTime = 25;} }
      else if(!dbg) {                                      // dbg: repeat bot don't chang ixTime 
        if( ++this.ixTime >= 30) { 
          this.ixTime = 0;
          if(++this.ixColorValueCtrl >= this.colorValueCtrl.length) {
            this.ixColorValueCtrl = 0;
          }
        } 
      }
      //
      if(this.ixTime ==28) {                         // waits a while without paint 
        this.dmaRdMaster.setNewPosition("8-2, 12+1");         // start position, first increment is done before usage
        for(int ix = 3; ix < 20; ++ix) {
          this.txDataSlave1[ix].set().data.color = this.colorWhite;
        }
        for(int ix = 3; ix < 20; ++ix) {
          this.txDataSlave2[ix].set().data.color = this.colorWhite;
        }
        for(GralCanvasStorage.Figure rx1 : this.rxDataSlave1 ) {
          rx1.set().data.color = this.colorWhite;
        }
        for(GralCanvasStorage.Figure rx1 : this.rxDataSlave2 ) {
          rx1.set().data.color = this.colorWhite;
        }
        for(GralCanvasStorage.Figure rx1 : this.rxDataMaster ) {
          rx1.set().data.color = this.colorWhite;
        }
      }
      //
      //------------------------------------------------ // data set in slave.
      switch(this.ixTime) {
      case 1: 
        for(int ixColor = 0; ixColor < 9; ++ixColor) {
          GralColor color = this.colorWordsRdSlave2[ixColor];
          if(color !=null) {
            this.txDataSlave2[ixColor].set().data.color = color;
        } }
        break;
      case 2: 
        for(int ixColor = 4; ixColor < 9; ++ixColor) {
          if(this.colorWordsRdSlave1[ixColor] !=null) {
            this.txDataSlave1[ixColor].set().data.color = this.colorWordsRdSlave1[ixColor];
        } }
        break;
      case 7:  
        for(int ixColor = 9; ixColor < 15; ++ixColor) {
          GralColor color = this.colorWordsRdSlave1[ixColor];
          if(color !=null) {
            this.txDataSlave1[ixColor].set().data.color = color;
        } }
        this.calcMaster1.set().bShow = true;
        break;
      case 8: 
        this.txDataMaster[12].set().data.color = this.rxDataMaster[1].data.color;
        break;
      case 10: 
        for(int ixColor = 10; ixColor < 19; ++ixColor) {
          GralColor color = this.colorWordsRdSlave2[ixColor];
          if(color !=null) {
            this.txDataSlave2[ixColor].set().data.color = color;
        } }
        this.calcMaster1.set().bShow = false;
        break;
      case 16: 
        this.dataEnvOutput.set().data.color = this.rxDataSlave1[12].data.color;
        this.prcsetOutput.show(true);
        break;
      case 17: 
        this.prcsetOutput.show(false);
        break;
      case 21: 
        for(int ixColor = 2; ixColor < 4; ++ixColor) {
          if(this.colorWordsRdSlave1[ixColor] !=null) {
            this.txDataSlave1[ixColor].set().data.color = this.colorWordsRdSlave1[ixColor];
        } }
        break;
      case 26:
        this.dataEnvMeas.set().data.color = this.colorValueCtrl[this.ixColorValueCtrl];
        break;
      case 27:
        this.txDataSlave1[0].set().data.color = this.dataEnvMeas.data.color;
        this.txDataSlave2[1].set().data.color = this.dataEnvMeas.data.color;
        this.prcgetMeas.set().bShow = true;
        break;
      case 28:
        this.prcgetMeas.set().bShow = false;
        break;
      } //switch

      //
      //------------------------------------------------ // serial shift
      if(this.ixTime >=3 && this.ixTime < 23) {
        this.rxDataMaster[this.ixTime-3].set().data.color = this.serialOutSlave2.data.color;  // move content from rx Port to rx data
      }
      //boolean bTxDataSlave2 = this.ixTime >=2 && this.ixTime < 22 && this.colorWordsRdSlave1[this.ixTime-2] !=null;
      boolean bTxDataSlave2 = this.ixTime >=2 && this.ixTime < 22 && this.txDataSlave2[this.ixTime-2].data.color != this.colorWhite;
      this.txSlaveSwitch2.setVariant(bTxDataSlave2 ? 1 : 0);
      if(this.ixTime >=2 && this.ixTime < 22) {
        GralColor colorRx = this.rxDataSlave2[this.ixTime-2].set().data.color = this.serialOutSlave1.data.color;  // move content from rx Port to rx data
        this.serialOutSlave2.set().data.color = bTxDataSlave2 ? this.txDataSlave2[this.ixTime-2].data.color  : colorRx;
      }
      //boolean bTxDataSlave1 = this.ixTime >=1 && this.ixTime < 21 && this.colorWordsRdSlave1[this.ixTime-1] !=null;
      boolean bTxDataSlave1 = this.ixTime >=1 && this.ixTime < 21 && this.txDataSlave1[this.ixTime-1].data.color != this.colorWhite;
      this.txSlaveSwitch1.setVariant(bTxDataSlave1 ? 1 : 0);
      if(this.ixTime >=1 && this.ixTime < 21) {
        GralColor colorRx = this.rxDataSlave1[this.ixTime-1].set().data.color = this.serialOutMaster1.data.color;  // move content from rx Port to rx data
        this.serialOutSlave1.set().data.color = bTxDataSlave1 ? this.txDataSlave1[this.ixTime-1].data.color  : colorRx;
      }
      GralColor colorWord;
      if(this.ixTime >=0 && this.ixTime <20) {
        colorWord = this.txDataMaster[this.ixTime].data.color; //this.colorWordsMaster[this.ixTime];
      } else if(this.ixTime >=21) {
        colorWord = this.colorWhite;
      } else {
        colorWord = this.serialOutMaster1.data.color;      // last step remain the color.
      }
      this.serialOutMaster1.set().data.color = colorWord; 
      //this.dmaRd1.setNewPosition(GralPos.refer, GralPos.samesize, GralPos.refer + 1.0f, GralPos.samesize);
      this.dmaRdMaster.bShow = this.ixTime>=0 && this.ixTime <20;
      this.dmaRdMaster.setNewPosition(6, 8, 12.0f+ this.ixTime, GralPos.size +1);
      this.dmaWrMaster.bShow = this.ixTime>=3 && this.ixTime <23;
      this.dmaWrMaster.setNewPosition(26, 28, 12.0f+ this.ixTime-3, GralPos.size +1);
      this.dmaWrSlave1.bShow = this.dmaRdSlave1.bShow = this.ixTime>=1 && this.ixTime <21;
      this.dmaWrSlave1.setNewPosition(6, 8, 40.0f+ this.ixTime-1.0f, GralPos.size +1);
      this.dmaRdSlave1.setNewPosition(26, 28, 40.0f+ this.ixTime-1.0f, GralPos.size +1);
      this.dmaWrSlave2.bShow = this.dmaRdSlave2.bShow = this.ixTime>=2 && this.ixTime <22;
      this.dmaWrSlave2.setNewPosition(6, 8, 70.0f+ this.ixTime-2.0f, GralPos.size +1);
      this.dmaRdSlave2.setNewPosition(26, 28, 70.0f+ this.ixTime-2.0f, GralPos.size +1);
      for(int ix = 0; ix < this.timeRef.length; ++ix) {
        int ixTime1 = this.ixTime +4;
        if(ixTime1 > 30) { ixTime1 -=30; }
        this.timeRef[ix].setNewPosition(38, GralPos.size -2, 8 + 30*ix + ixTime1, GralPos.size + 1);
      }
      //
      this.plotArea.redraw(20, 100, true);
      Debugutil.stop();
    }
  }
  
  
  
  
  void exec() throws ParseException {
      init();
      while(this.gralMng.isRunning()) {
        try {
          int msWait = this.btnSlow.getState() == GralButton.State.On ? 1000 : 100;
          Thread.sleep(msWait);
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
