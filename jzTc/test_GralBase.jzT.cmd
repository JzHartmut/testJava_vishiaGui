


==JZtxtcmd==

currdir = <:><&scriptdir>/..<.>;

main(){
  cd &currdir;
  java org.vishia.stimuliSelector.StimuliSelector.main("appl/StimuliSelect_Example/StimuliSelector.jzT.cmd", "-size:C");
  cd &currdir;                                                ## because the stimuliSelector also changes the current dir
  java org.vishia.gral.test.basics.Test_GralArea9.main();
  java org.vishia.gral.test.basics.Test_WindMenu.main("SWT");
  java org.vishia.gral.test.basics.Test_GralTableAppl.main();
  java org.vishia.gral.test.basics.Test_SimpleTextButton.main("SWT");
  java org.vishia.gral.test.appl.Test_GuiCfg.main("appl/ViewCfg/");  ## local dir necessary
  java org.vishia.gral.test.appl.Show_Movie_SpeTelg.main();

}

