%====================================================================================
% monitoringdevice description   
%====================================================================================
event( sonardata, distance(DISTANCE) ).
event( stateSonar, stateSonar(FULL) ).
dispatch( sonarstart, sonarstart(X) ).
dispatch( sonarwork, sonarwork(X) ).
dispatch( sonarstop, sonarstop(X) ).
dispatch( ledOn, ledOn(X) ).
dispatch( ledOff, ledOff(X) ).
dispatch( ledBlink, ledBlink(X) ).
dispatch( doread, doread(X) ).
%====================================================================================
context(ctxmd, "localhost",  "TCP", "8128").
 qactor( monitoringdevice, ctxmd, "it.unibo.monitoringdevice.Monitoringdevice").
 static(monitoringdevice).
  qactor( datacleaner, ctxmd, "it.unibo.datacleaner.Datacleaner").
 static(datacleaner).
  qactor( sonardevice, ctxmd, "it.unibo.sonardevice.Sonardevice").
 static(sonardevice).
  qactor( warningdevice, ctxmd, "it.unibo.warningdevice.Warningdevice").
 static(warningdevice).
