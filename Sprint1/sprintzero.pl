%====================================================================================
% sprintzero description   
%====================================================================================
dispatch( goHome, goHome(X) ).
dispatch( bringRP, bringRP(X) ).
dispatch( bringAsh, bringAsh(X) ).
dispatch( act, act(X) ).
dispatch( notifyRp, notifyRp(x) ).
dispatch( ack, ack(X) ).
dispatch( atIncinerator, atIncinerator(X) ).
dispatch( ashDeposited, ashDeposited(X) ).
dispatch( newRp, newRp(X) ).
dispatch( rpTaken, rpTaken(X) ).
dispatch( newAsh, newAsh(X) ).
dispatch( emptyAsh, emptyAsh(X) ).
event( burnEnd, burnEnd(BTIME) ).
event( stateScale, stateScale(X) ).
event( stateSonar, stateSonar(X) ).
request( engage, engage(OWNER,STEPTIME) ).
reply( engagedone, engagedone(ARG) ).  %%for engage
reply( engagerefused, engagerefused(ARG) ).  %%for engage
dispatch( disengage, disengage(ARG) ).
request( moverobot, moverobot(X,Y) ).
reply( moverobotdone, moverobotdone(X) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(X) ).  %%for moverobot
%====================================================================================
context(ctxwis, "localhost",  "TCP", "8001").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( incinerator, ctxwis, "it.unibo.incinerator.Incinerator").
 static(incinerator).
  qactor( scalemock, ctxwis, "it.unibo.scalemock.Scalemock").
 static(scalemock).
  qactor( sonarmock, ctxwis, "it.unibo.sonarmock.Sonarmock").
 static(sonarmock).
  qactor( oprobot, ctxwis, "it.unibo.oprobot.Oprobot").
 static(oprobot).
  qactor( wis, ctxwis, "it.unibo.wis.Wis").
 static(wis).
