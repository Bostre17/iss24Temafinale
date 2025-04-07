%====================================================================================
% sprintzero description   
%====================================================================================
event( burnEnd, burnEnd(BTIME) ).
%====================================================================================
context(ctxwis, "localhost",  "TCP", "8001").
context(ctxbasicrobot, "127.0.0.2",  "TCP", "8002").
context(ctxmonitoringdevice, "127.0.0.1",  "TCP", "8003").
 qactor( ddr_robot, ctxbasicrobot, "external").
  qactor( monitoringdevice, ctxmonitoringdevice, "it.unibo.monitoringdevice.Monitoringdevice").
 static(monitoringdevice).
  qactor( wis, ctxwis, "it.unibo.wis.Wis").
 static(wis).
  qactor( oprobot, ctxwis, "it.unibo.oprobot.Oprobot").
 static(oprobot).
  qactor( incinerator, ctxwis, "it.unibo.incinerator.Incinerator").
 static(incinerator).
