if not exist %TMP%\Eclipse_tmp mkdir %TMP%\Eclipse.tmp
if not exist %TMP%\Eclipse_tmp\vishiaGui_pj-bin mkdir %TMP%\Eclipse_tmp\vishiaGui_pj-bin
if not exist bin mklink /J bin %TMP%\Eclipse_tmp\vishiaGui_pj-bin

