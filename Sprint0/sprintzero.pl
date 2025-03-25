%====================================================================================
% sprintzero description   
%====================================================================================
event( burnEnd, burnEnd(X) ).
event( startUp, startUp(X) ).
event( burning, burning(X) ).
event( finishedBurning, finishedBurning(X) ).
event( startIncinerator, startIncinerator(X) ).
event( startBurning, startBurning(X) ).
event( ashMeasurement, ashMeasurement(level) ).
%====================================================================================
context(ctxservicearea, "localhost",  "TCP", "8016").
 qactor( wis, ctxservicearea, "it.unibo.wis.Wis").
 static(wis).
  qactor( oprobot, ctxservicearea, "it.unibo.oprobot.Oprobot").
 static(oprobot).
  qactor( incinerator, ctxservicearea, "it.unibo.incinerator.Incinerator").
 static(incinerator).
