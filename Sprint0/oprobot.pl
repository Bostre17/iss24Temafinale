%====================================================================================
% oprobot description   
%====================================================================================
event( burnEnd, burnEnd(BTIME) ).
dispatch( go, go(ACTION) ).
%====================================================================================
context(ctxoprobot, "localhost",  "TCP", "8002").
 qactor( oprobot, ctxoprobot, "it.unibo.oprobot.Oprobot").
 static(oprobot).
