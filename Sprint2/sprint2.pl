%====================================================================================
% sprint2 description   
%====================================================================================
dispatch( burn, burn(X) ).
event( burnEnd, burnEnd(BTIME) ).
request( cmd, on(CMD) ).
dispatch( goWasteIn, go(X) ).
dispatch( goBurnOut, go(X) ).
dispatch( goHome, go(X) ). %messaggio da parte di WIS per ordinare al robot di tornare a casa
event( updateStateRobot, update(POSX,POSY,JOB) ).
request( engage, engage(OWNER,STEPTIME) ).
reply( engagedone, engagedone(ARG) ).  %%for engage
reply( engagerefused, engagerefused(ARG) ).  %%for engage
request( moverobot, moverobot(TARGETX,TARGETY) ).
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
event( updateWS, updateWS(WEIGHT) ).
event( updateAS, updateAS(STATE) ).
dispatch( robotArrived, robotArrived(X) ).
dispatch( endRoutine, endRoutine(X) ).
dispatch( arrivoRobot, p(X) ).
dispatch( newAsh, ash(X) ).
request( testASreq, test(WEIGHT) ).
reply( testAS, test(DISTANCE) ).  %%for testASreq
dispatch( newRP, newRP(WEIGHT) ).
dispatch( sonarstart, sonarstart(X) ).
dispatch( sonarwork, sonarwork(X) ).
dispatch( sonarstop, sonarstop(X) ).
%====================================================================================
context(ctxwis, "localhost",  "TCP", "8001").
context(ctxddr, "127.0.0.1",  "TCP", "8020").
context(ctxmd, "192.168.1.15",  "TCP", "8128").
 qactor( basicrobot, ctxddr, "external").
  qactor( sonar24, ctxmd, "external").
  qactor( incenerator, ctxwis, "it.unibo.incenerator.Incenerator").
 static(incenerator).
  qactor( oprobot, ctxwis, "it.unibo.oprobot.Oprobot").
 static(oprobot).
  qactor( wis, ctxwis, "it.unibo.wis.Wis").
 static(wis).
  qactor( scale, ctxwis, "it.unibo.scale.Scale").
 static(scale).
  qactor( tester, ctxwis, "it.unibo.tester.Tester").
 static(tester).
