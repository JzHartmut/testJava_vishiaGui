package org.vishia.gral.test.basics;

import java.text.ParseException;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;
import org.vishia.util.ExcUtil;
import org.vishia.util.StringFunctions;
import org.vishia.util.TestOrg;

public class Test_GralPos {

  LogMessage log = new LogMessageStream(System.out);       // write some logs here.
  GralMng gralMng = new GralMng(this.log);                      // need to register panels. The panel "screen" is known.
  
  String pos1 = "@10+2, 12+10";
  String pos2 = "@panel, 10+2,12+10";
  
  String[][] tests = 
    { { "2..4, 0..12"          , "@panel, 2..4, 0..12"            , "(0 + 119, 20 + 19)", "absolute positions from top and left, simplest case"}
    , { "2.5..4.7, 0.4..12"    , "@panel, 2.5..4.7, 0.4..12"      , "(4 + 115, 25 + 21)", "absolute positions from top and left but with fractional parts"}
    , { "@panel, 10+2,12+10"   , "@panel, 10..12, 12..22"         , "(120 + 99, 100 + 19)", "absolute position with size, given with existing panel"}
    , { "+2.3-2.1, +11.4+7.5"   , "@panel, 12.2..14.3, 23.4..30.9" , "(234 + 74, 122 + 20)", "relative position  with size for both"}
    , { "10-2,12+10"           , "@panel, 8..10, 12..22"          , "(120 + 99, 80 + 19)",  "absolute position with negative height for bottom line orientation of the position"}
    //5
    , { "+-1.5-1.9, +10+8"      , "@panel, 4.6..6.5, 22..30"       , "(220 + 79, 46 + 18)",  "relative position for line negative ->above, with negative heigth, column relative to right"}
    , { "-10+2,-12+10"         , "@panel, -10..-8, -12..-2"       , "(880 + 99, 900 + 19)", "absolute position from bottom and rigth with size"}
    , { "10..12,-12..-0.5"     , "@panel, 10..12, -12..-0.5"      , "(880 + 114, 100 + 19)", "absolute positions from top, absolute positions from right, very less"}
    , { "10..12,-12..-1.9"     , "@panel, 10..12, -12..-1.9"      , "(880 + 100, 100 + 19)", "absolute positions from top, absolute positions from right"}
    , { "10..12,-12..0"        , "@panel, 10..12, -12..0"         , "(880 + 119, 100 + 19)", "absolute position from top, absolute positons from right till end "}
    //10
    , { "@10++,24+6"           , "@panel, 10..12, 24..30"         , "(240 + 59, 100 + 19)", "next line for the maybe next position. height not given, use height previous"}
    , { "@,+8+4"               , "@panel, 10..12, 32..36"         , "(320 + 39, 100 + 19)", "line not given, use same line as previous, not next line because position is given, use relative column"}
    , { ""                     , "@panel, 10..12, 32..36"         , "(320 + 39, 100 + 19)", "nothing given, use same position"}
    , { "@+"                   , "@panel, 12..14, 32..36"         , "(320 + 39, 120 + 19)", "nothing given, use same position"}
    , { null                   , "@panel, 14..16, 32..36"         , "(320 + 39, 140 + 19)", "null given, use same size in next position"}
      
    };
  
  void test1( TestOrg testParent) {
    TestOrg test = new TestOrg("Test_GralPol1", 2, testParent);
    
    GralPos refPos = new GralPos(this.gralMng);          // an not determined pos, the default panel is the screen. 
    //---------------------------------------------------  // creates a window, it may be in the top level window, with relative coord. to the screen.
    GralWindow window = new GralWindow(refPos, "@screen,10+60, 20+100=panelWindow", "Test Window", 0);
    //---------------------------------------------------  // now "panel" can be used as panel name in position strings.
    //                                                     // the refPos is related to the main panel of the window.
    this.gralMng.gralProps.setSizeGui('E');                // size E means, 1 fine grid = 1 pixel

    try {
    StringBuilder sbPos = new StringBuilder();
    int p = 5;
      GralPos.appendPos(sbPos, p);
      test.expect(sbPos, "0.5", 5, "appendPos, inp: " + p + ", result:" + sbPos);
      sbPos.setLength(0);
      p = 15;
      GralPos.appendPos(sbPos, p);
      test.expect(sbPos, "1.5", 5, "appendPos, inp: " + p + ", result:" + sbPos);
      sbPos.setLength(0);
      p = -5;
      GralPos.appendPos(sbPos, p);
      test.expect(sbPos, "-0.5", 5, "appendPos, inp: " + p + ", result:" + sbPos);
      sbPos.setLength(0);
      p = -15;
      GralPos.appendPos(sbPos, p);
      test.expect(sbPos, "-1.5", 5, "appendPos, inp: " + p + ", result:" + sbPos);
    } catch (Exception exc){
      test.exception(exc);
    }
    
    
    
    for(int ix = 0; ix < this.tests.length; ++ix) {
      String posString = this.tests[ix][0];
      try {
        refPos.dbg = false;
        if(ix == -1) {
          refPos.dbg = true;
          Debugutil.stop();
        }
        refPos.setPosition(posString, null);
      } catch (Exception exc) {
        test.exception(exc);
      }
      checkPosition(test, refPos, ix, posString, this.tests[ix][1], this.tests[ix][2], this.tests[ix][3]);
    }
    window.remove();
    test.finish();
  }
  
  
  void checkPosition(TestOrg test, GralPos refPos, int ix, String posString, String sExpectPos, String sExpectPix, String sTestDescr) {
    GralRectangle pix = this.gralMng.calcWidgetPosAndSize(refPos, 1000, 1000, 0,0);
    String sPos = refPos.toString();
    //int eq = test.expect(sPos, sExpectPos, 5, posString + ": " + sPos + " != " + sExpectPos + ": "+ this.tests[ix][3]);
    int eq = StringFunctions.comparePos(sPos, sExpectPos);
    test.expect(eq ==0, 5, "[" + ix + "] " + posString + ", result: " + sPos + ", expected: " + sExpectPos + ", "+ sTestDescr);
    String sPix = pix.toString().substring(13);
    int eqpix = StringFunctions.comparePos(sPix, sExpectPix);
    test.expect(eqpix ==0, 5, "[" + ix + "] " + posString + ", result: " + sPix + ", expected: " + sExpectPix + ": "+ sTestDescr);
    if(eq !=0 || eqpix !=0) {
      Debugutil.stop();
    }
  }
  
  
  
  void test2( TestOrg testParent) {
   TestOrg test = new TestOrg("Test_GralPos2", 2, testParent);
    
    GralPos refPos = new GralPos(this.gralMng);          // an not determined pos, the default panel is the screen. 
    //---------------------------------------------------  // creates a window, it may be in the top level window, with relative coord. to the screen.
    GralWindow window = new GralWindow(refPos, "@screen,10+60, 20+100=panelWindow", "Test Window", 0);
    //---------------------------------------------------  // now "panel" can be used as panel name in position strings.
    //                                                     // the refPos is related to the main panel of the window.
    this.gralMng.gralProps.setSizeGui('E');                // size E means, 1 fine grid = 1 pixel
  
    refPos.setPosition(refPos, 10, 12, 5, 20);             
    checkPosition(test, refPos, 1, "", "@panel, 10..12, 5..20", "(50 + 149, 100 + 19)", "sets a basic fix position");
    //
    refPos.setPosition(refPos, 10.0f, -20.5f, 5, -0.5f);
    checkPosition(test, refPos, 2, "", "@panel, 10..-20.5, 5..-0.5", "(50 + 944, 100 + 694)", "sets a fractional fix positon from bottom");
    //
    refPos.setPosition(refPos, 10, GralPos.size - 2, 0, 0);
    checkPosition(test, refPos, 3, "", "@panel, 8..10, 0..0", "(0 + 999, 80 + 19)", "sets a basic position with size");
    //
    refPos.setPosition(refPos, GralPos.refer + 2, GralPos.size - 2, 50, GralPos.size-2.4f, 0, 'd', 0);
    checkPosition(test, refPos, 4, "", "@panel, 10..12, 47.6..50", "(476 + 23, 100 + 19)", "sets a position with refer to line and size, with direction down");
    //
    refPos.setPosition(refPos, GralPos.next, GralPos.samesize, GralPos.same, GralPos.samesize);
    checkPosition(test, refPos, 5, "", "@panel, 12..14, 47.6..50", "(476 + 23, 120 + 19)", "sets a postion with next down and same column");
    refPos.setUsed();                                      // same as used for a widget
    //
    refPos.checkSetNext();                                 // same as no position given for a new widget
    checkPosition(test, refPos, 6, "", "@panel, 14..16, 47.6..50", "(476 + 23, 140 + 19)", "sets a next position automatically (not with setPosition(...)");
    //
    //The next operation is not recommended, use the float version. 
    refPos.setFinePosition(0, 5, GralPos.samesize, 0, 20, 3, -10, 7, 0, 'r', 0, 5, refPos);
    checkPosition(test, refPos, 6, "", "@panel, 0.5..2.5, 20.3..-9.3", "(203 + 703, 5 + 19)", "sets using setFinePosition(...) with integer grid and fine grid");
    window.remove();
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
    thiz.test2(test);
    test.finish();
    
  }
}
