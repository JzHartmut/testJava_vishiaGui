package org.vishia.gral.test.appl;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.test.GralPlotWindow;
import org.vishia.gral.widget.GralPlotArea;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;

public class Show_Movie_SpeTelg {

  final GralMng gralMng = new GralMng(null);
  
  final GralPos pos = new GralPos(this.gralMng, "@screen,10..80,10..120");
  
  final GralWindow wind = new GralWindow(this.pos, "SpeTelgWindow", "SPE data transfer"
                        , GralWindow_ifc.windResizeable , this.gralMng);
  
  final GralPlotArea plotArea = new GralPlotArea(this.pos, "@SpeTelg,0..-10,0..0=canvas");
  
  final GralCanvasStorage plot = this.plotArea.getCanvasStore(0);
  
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
  final GralCanvasStorage.FigureDataSet figData_Words = new GralCanvasStorage.FigureDataSet("words"); { 
    GralColor color = GralColor.getColor("bk");
    this.figData_Words.addPolyline(color).point(0, 0).point(0,2);  // |
    for(int ix = 0; ix <20; ++ix) {
      this.figData_Words.addPolyline(color)
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
  final GralCanvasStorage.FigureDataSet figData_RefTop = new GralCanvasStorage.FigureDataSet("refArrow"); { 
    GralColor color = GralColor.getColor("drd");
    this.figData_RefTop.addPolyline(color).point(0, 0).point(0, 1.9f);  //                |
    this.figData_RefTop.addPolyline(color).point(-0.4f, 1).point(0, 2).point(0.4f, 1);  // ^
  }
  
  
  
  final GralCanvasStorage.FigureDataSet figData_serialRight = new GralCanvasStorage.FigureDataSet("serialRight"); { 
    GralColor color = GralColor.getColor("bk");
    this.figData_serialRight.addPolyline(color).point( 0, 0.3f).point(24, 0.3f);    // ==========
    this.figData_serialRight.addPolyline(color).point( 0, -0.3f).point(24, -0.3f);  // 
    this.figData_serialRight.addPolyline(color).point(23.0f, 0.5f).point(24.3f, 0.0f).point(23.0f, -0.5f);  // ==>
  }
  
  /**Contains only the fillin color.
   * 
   */
  GralCanvasStorage.Figure[] rxSlave1 = new GralCanvasStorage.Figure[20]; 
  
  
  float posSerialRef;
  
  boolean bCont = true;

  /**Reference arrow in Ring-Master
   * 
   */
  GralCanvasStorage.Figure ref1;
  
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
  
  
  
  void init() throws ParseException {
    //
    this.pos.setPosition("10-2,10+1++");
    this.plot.addFigure("dataWordsMaster", this.pos, this.figData_Words, false);
    
    this.pos.setPosition("10-2,40+1++");
    this.plot.addFigure("dataWordsSlave1", this.pos, this.figData_Words, false);

    for(int ix = 0; ix < 20; ++ix) {                       // color (content) of the data words slave
      this.rxSlave1[ix] = this.plot.addFigure("rxSlave1-" + ix, this.pos, new GralCanvasStorage.Fillin(colorWhite), false);
      this.pos.setPosition(",+1");
    }
    
    
    //------------------------------------------ // 20 colors for Master word
    for(int ix = 0; ix <20; ++ix) {             
      int red = (ix%2==1) ? 0xc0: 0x80;
      GralColor colorFill = GralColor.getColor(red, (19-ix)*10 + 65, ix*10 + 65);
      this.colorWordsMaster[ix] = colorFill;
    }
    this.pos.setPosition("10-2, 10+1++");
    for(int ix = 0; ix <20; ++ix) {             //4 points per word  --+
      this.plot.drawFillin(this.pos, this.colorWordsMaster[ix]);
      this.pos.setNextPosition();
    }
    //GralPlotArea.UserUnits units = this.plot.userUnitsPerGrid(0.0f,  0.0f,  1.0f,  1.0f);
    //
    //create the figure for the read reference arrow:
    this.pos.setPosition("12-2, 10.5+1");
    this.ref1 = this.plot.addFigure("refData_Master", this.pos, this.figData_RefTop, true);
    //
    //GralCanvasStorage.Figure serialOut1Line =            // The ====> line for serial out
    this.plot.addFigure("serialOutMaster", this.pos, this.figData_serialRight, false);
    //
    this.pos.setPosition("13-2, 35+1");                    // value on the serial plug
    GralColor colorFillin = this.colorWordsMaster[8];
    this.serialOut1word = this.plot.addFigure("wordTxMaster", this.pos, new GralCanvasStorage.Fillin(colorFillin), false);
    //
    LogMessage log = new LogMessageStream(System.out);
    this.gralMng.createGraphic("SWT", 'B', log);
    
    this.plotArea.repaint(100, 100);
  }
  
  
  
  /**Step to show moving in the image. Called any 100 ms.
   * @throws ParseException
   */
  void step() throws ParseException {
    this.ref1.move(5, 0);                                  // move 0.5 grid to right
    if((this.posSerialRef +=0.5) >=24) { 
      if(this.posSerialRef >=25) {                         // waits a while without paint 
        this.posSerialRef = 0;                             // then starts from 0 again
        this.ref1.pos.setPosition("12-2, 10.0+1");         // start position, first increment is done before usage
        for(GralCanvasStorage.Figure rx1 : this.rxSlave1 ) {
          rx1.data.color = this.colorWhite;
        }
        this.plotArea.repaint();
        Debugutil.stop();
      }
    }
    else {                                                 // shows the output.
      int ixColor = (int)this.posSerialRef;                // the ix of the serial out master
      if(ixColor >=1 && ixColor < 21) {
        this.rxSlave1[ixColor-1].data.color = this.serialOut1word.data.color;  // move content from rx Port to rx data
      }
      GralColor colorWord;
      if(ixColor >=0 && ixColor <20) {
        colorWord = this.colorWordsMaster[ixColor];
      } else if(ixColor >=21) {
        colorWord = this.colorWhite;
      } else {
        colorWord = this.serialOut1word.data.color;        // last step remain the color.
      }
      this.serialOut1word.data.color = colorWord; 
      this.ref1.pos.setPosition(this.ref1.pos, GralPos.refer, GralPos.samesize, GralPos.refer + 0.5f, GralPos.samesize);
      
      this.plotArea.repaint();
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
