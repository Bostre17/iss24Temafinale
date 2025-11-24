%====================================================================================
% sprinttwo description   
%====================================================================================
dispatch( goHome, goHome(X) ).
request( bringRP, bringRP(X) ).
request( bringAsh, bringAsh(X) ).
dispatch( act, act(X) ).
dispatch( notifyRp, notifyRp(x) ).
dispatch( ack, ack(X) ).
reply( atIncinerator, atIncinerator(X) ).  %%for bringRP
reply( ashDeposited, ashDeposited(X) ).  %%for bringAsh
dispatch( newRp, newRp(X) ).
dispatch( rpTaken, rpTaken(X) ).
event( burnEnd, burnEnd(BTIME) ).
event( stateScale, stateScale(X) ).
event( stateSonar, stateSonar(X) ).
event( stateLed, stateLed(X) ).
request( engage, engage(OWNER,STEPTIME) ).
reply( engagedone, engagedone(ARG) ).  %%for engage
reply( engagerefused, engagerefused(ARG) ).  %%for engage
dispatch( disengage, disengage(ARG) ).
request( moverobot, moverobot(X,Y) ).
reply( moverobotdone, moverobotdone(X) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(X) ).  %%for moverobot
request( startTest, startTest(PAYLOAD) ).
reply( testReply, testReply(RESULT) ).  %%for startTest
dispatch( sonarstart, sonarstart(X) ).
dispatch( sonarstop, sonarstop(X) ).
%====================================================================================
context(ctxwis, "localhost",  "TCP", "8001").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxmd, "192.168.1.15",  "TCP", "8128").
 qactor( monitoringdevice, ctxmd, "external").
  qactor( basicrobot, ctxbasicrobot, "external").
  qactor( incinerator, ctxwis, "it.unibo.incinerator.Incinerator").
 static(incinerator).
  qactor( scalemock, ctxwis, "it.unibo.scalemock.Scalemock").
 static(scalemock).
  qactor( oprobot, ctxwis, "it.unibo.oprobot.Oprobot").
 static(oprobot).
  qactor( wis, ctxwis, "it.unibo.wis.Wis").
 static(wis).
