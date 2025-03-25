%====================================================================================
% wis description   
%====================================================================================
event( burnEnd, burnEnd(BTIME) ).
%====================================================================================
context(ctxincinerator, "localhost",  "TCP", "8001").
 qactor( incenerator, ctxincinerator, "it.unibo.incenerator.Incenerator").
 static(incenerator).
