package org.vishia.gral.test.basics;

import java.text.ParseException;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;
import org.vishia.util.ExcUtil;
import org.vishia.util.TestOrg;

public class Test_GralPos {

  LogMessage log = new LogMessageStream(System.out);       // write some logs here.
  GralMng gralMng = new GralMng(this.log);                      // need to register panels. The panel "screen" is known.
  
  String pos1 = "@10+2, 12+10";
  String pos2 = "@panel, 10+2,12+10";
  
  String[][] tests = 
    { { "@panel, 10+2,12+10" , "panel=panel, line=10.0..12.0 col=12.0..22.0 .rtl", "absolute position with size, given with existing panel"}
    , { "10-2,12+10"     , "panel=panel, line=8.0..10.0 col=12.0..22.0 .rtl"    , "absolute position related to bottom line, with size"}
    , { "-10+2,-12+10"   , "panel=panel, line=-10.0..-8.0 col=-12.0..-2.0 .rtl" , "absolute position from bottom and rigth with size"}
    , { "10..12,-12..-2" , "panel=panel, line=10.0..12.0 col=-12.0..-2.0 .rtl"  , "absolute position from left and bottom absolute size"}
    , { "10..12,-12..20" , "panel=panel, line=10.0..12.0 col=-12.0..20.0 .rtl"  , "absolute position from buttom but end from top, formally correct, but problematically"}
      //5
    , { "@10,24+6"       , "panel=panel, line=10.0..12.0 col=24.0..30.0 .rtl"   , "height not given, use height previous"}
    , { "@,32+4"         , "panel=panel, line=10.0..12.0 col=32.0..36.0 .rtl"   , "line not given, use height previous"}
    , { ""               , "panel=panel, line=10.0..12.0 col=36.0..40.0 .rtl"   , "nothing given, use same size in next position"}
    , { null             , "panel=panel, line=10.0..12.0 col=40.0..44.0 .rtl"   , "null given, use same size in next position"}
      
    };
  
  void test1( TestOrg testParent) {
    TestOrg test = new TestOrg("Test_GralPol1", 2, testParent);
    
    GralPos posPanel = new GralPos(this.gralMng);          // an not determined pos, the default panel is the screen. 
    //---------------------------------------------------  // creates a panel, it may be in the top level window, with relative coord. to the screen.
    GralPanelContent panel = new GralPanelContent(posPanel, "@screen,10+60, 20+100=panel", gralMng);
    //---------------------------------------------------  // now "panel" can be used in position strings.
    GralPos pos = new GralPos(panel);                      // The posPanel can be used also.
    
    for(int ix = 0; ix < this.tests.length; ++ix) {
      String posString = this.tests[ix][0];
      try {
        if(ix == 7)
          Debugutil.stop();
        pos.setPosition(posString, null);
      } catch (Exception exc) {
        test.exception(exc);
      }
      String sPos = pos.toString();
      String sExpectPos = this.tests[ix][1];
      int eq = test.expect(sPos, sExpectPos, 5, posString + ": " + this.tests[ix][2]);
      if(eq !=0) {
        Debugutil.stop();
      }
    }
    test.finish();
  }
  
  
  public void testScanSize(){
    GralPos posParent = new GralPos(this.gralMng);
    GralPos posTest = new GralPos(this.gralMng);
    try{
      posParent.setPosition("@3+2, 10+10", null);  //line 3, column 10 + 10
      posTest.setPosition(",+12", posParent);       //line 3, column 20 + 12
      ExcUtil.checkMsg(posParent.y.p1 == 3 && posParent.y.p2 == 5 && posParent.x.p1 == 10 && posParent.x.p2 == 20 , "posParent failure");
      ExcUtil.checkMsg(posTest.y.p1 == 3   && posTest.y.p2 == 5   && posTest.x.p1 == 20   && posTest.x.p2 == 32 , "posTest failure");  //next, size=12
    } catch(ParseException exc) {
      System.err.println("GralPos.testScanSize - error, " + exc.getMessage());
    }
    
  }

  public static void main(String[] args) {
    TestOrg test = new TestOrg("Test_GralPos", 1, args);
    Test_GralPos thiz = new Test_GralPos();
    
    thiz.test1(test);
    test.finish();
    
  }
}
