%====================================================================================
% oprobot description   
%====================================================================================
dispatch( go, go(ACTION) ).
dispatch( status, status(CONDITION) ).
%====================================================================================
context(ctxoprobot, "localhost",  "TCP", "8030").
 qactor( oprobot, ctxoprobot, "it.unibo.oprobot.Oprobot").
 static(oprobot).
