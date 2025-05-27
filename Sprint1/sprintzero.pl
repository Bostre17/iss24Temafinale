%====================================================================================
% sprintzero description   
%====================================================================================
dispatch( goHome, goHome(X) ).
dispatch( bringRP, bringRP(X) ).
dispatch( bringAsh, bringAsh(X) ).
dispatch( act, act(X) ).
dispatch( ack, ack(X) ).
dispatch( atIncinerator, atIncinerator(X) ).
dispatch( ashDeposited, ashDeposited(X) ).
event( stateScale, stateScale(X) ).
event( stateSonar, stateSonar(x) ).
event( stateIncinerator, stateIncinerator(X) ).
event( position, position(X,Y) ).
event( burnEnd, burnEnd(BTIME) ).
%====================================================================================
context(ctxwis, "localhost",  "TCP", "8001").
context(ctxscale, "localhost",  "TCP", "8002").
context(ctxsonar, "localhost",  "TCP", "8003").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8001").
 qactor( ddr_robot, ctxbasicrobot, "external").
  qactor( wis, ctxwis, "it.unibo.wis.Wis").
 static(wis).
  qactor( incinerator, ctxwis, "it.unibo.incinerator.Incinerator").
 static(incinerator).
  qactor( scale, ctxscale, "it.unibo.scale.Scale").
 static(scale).
  qactor( sonar, ctxsonar, "it.unibo.sonar.Sonar").
 static(sonar).
  qactor( oprobot, ctxwis, "it.unibo.oprobot.Oprobot").
 static(oprobot).
