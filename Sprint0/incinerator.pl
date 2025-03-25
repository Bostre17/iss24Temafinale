%====================================================================================
% incinerator description   
%====================================================================================
event( startUp, startUp(BTIME) ).
event( startBurning, startBurning(BTIME) ).
event( burning, burning(start) ).
event( finishedBurning, finishedBurning(complete) ).
event( burnEnd, burnEnd(stop) ).
%====================================================================================
context(ctxincinerator, "localhost",  "TCP", "8001").
 qactor( incinerator, ctxincinerator, "it.unibo.incinerator.Incinerator").
 static(incinerator).
