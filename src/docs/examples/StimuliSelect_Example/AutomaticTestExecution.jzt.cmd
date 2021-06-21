set LIBSPATH=./
echo on 
java -cp %LIBSPATH%/libs/vishiaBase.jar org.vishia.jztxtcmd.JZtxtcmd %0
echo off
pause
exit /b

                            
==JZtxtcmd==

include StimuliSelector.jzT.cmd;

currdir=<:><&scriptdir><.>;


main() {

  Obj jztc.envar.soRx = null;

  call genTestCaseThread(select = "1=v1; 2=t1; 3=A1, A2, A3, A4; 4=B1; 5=C1; " );

  call genTestCaseThread(select = <:>1=v2, v3; 2=t1, t2; 3=A1; 4=B1; 5=C1;
                                     : 1=v1; 2=t1, t2; 3=A1, A2; 4=B1; 5=C3;<.> );                                   

  call genTestCaseThread(select = <:>1=v2, v3; 4=B1, B3; + 1=v1; 4=B2
                                     & 2=t1,t2; 3=A1; + 2=t2; 3=A3;
                                     & 5=C1<.> );

}