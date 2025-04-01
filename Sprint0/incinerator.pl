%====================================================================================
% incinerator description   
%====================================================================================
event( burnEnd, burnEnd(BTIME) ).
%====================================================================================
context(ctxincinerator, "localhost",  "TCP", "8001").
 qactor( incinerator, ctxincinerator, "it.unibo.incinerator.Incinerator").
 static(incinerator).
