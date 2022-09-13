package org.vishia.gral.test.basics;

import java.text.ParseException;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.util.Debugutil;
import org.vishia.util.TestOrg;

public class Test_GralPos {

  
  String pos1 = "@10+2, 12+10";
  String pos2 = "@panel, 10+2,12+10";
  
  String[][] tests = 
    { { "@panel, 10+2,12+10" , "panel=GralPanel:panel, line=10.0..12.0 col=12.0..22.0 r.tl", "absolute position with size, with panel"}
    , { "10-2,12+10"     , "panel=GralPanel:panel, line=8.0..10.0 col=12.0..22.0 r.tl"    , "absolute position from rigth and buttom with size"}
    , { "-10+2,-12+10"   , "panel=GralPanel:panel, line=-10.0..-8.0 col=-12.0..-2.0 r.tl" , "absolute position from rigth and buttom with size"}
    , { "10..12,-12..-2" , "panel=GralPanel:panel, line=10.0..12.0 col=-12.0..-2.0 r.tl"  , "absolute position from left and buttom absolute size"}
    , { "10..12,-12..20" , "panel=GralPanel:panel, line=10.0..12.0 col=-12.0..20.0 r.tl"  , "absolute position from buttom but end from top, formally correct, but problematically"}
      //5
    , { "@10,24+6"       , "panel=GralPanel:panel, line=10.0..12.0 col=24.0..30.0 r.tl"   , "height not given, use height previous"}
    , { "@,32+4"         , "panel=GralPanel:panel, line=10.0..12.0 col=32.0..36.0 r.tl"   , "line not given, use height previous"}
    , { ""               , "panel=GralPanel:panel, line=10.0..12.0 col=36.0..40.0 r.tl"   , "nothing given, use same size in next position"}
    , { null             , "panel=GralPanel:panel, line=10.0..12.0 col=40.0..44.0 r.tl"   , "null given, use same size in next position"}
      
    };
  
  void test1( TestOrg testParent) {
    TestOrg test = new TestOrg("Test_GralPol1", 2, testParent);
    
    GralMng gralMng = GralMng.get();                       // needs the singleton to register panels.
    
    GralPanelContent panel = new GralPanelContent(null, "panel");
    GralPos pos = new GralPos();
    
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
  
  public static void main(String[] args) {
    TestOrg test = new TestOrg("Test_GralPos", 1, args);
    Test_GralPos thiz = new Test_GralPos();
    
    thiz.test1(test);
    test.finish();
    
  }
}
