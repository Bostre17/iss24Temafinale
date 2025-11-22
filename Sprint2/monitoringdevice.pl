%====================================================================================
% monitoringdevice description   
%====================================================================================
event( sonardata, distance(D) ).
event( updateAS, updateAS(D) ).
dispatch( sonarstart, sonarstart(X) ).
dispatch( sonarwork, sonarwork(X) ).
dispatch( sonarstop, sonarstop(X) ).
dispatch( doread, doread(X) ).
%====================================================================================
context(ctxmd, "localhost",  "TCP", "8128").
context(ctxwis, "127.0.0.1",  "TCP", "8001").
 qactor( wis, ctxwis, "external").
  qactor( monitoringdevice, ctxmd, "it.unibo.monitoringdevice.Monitoringdevice").
 static(monitoringdevice).
  qactor( datacleaner, ctxmd, "it.unibo.datacleaner.Datacleaner").
 static(datacleaner).
  qactor( sonardevice, ctxmd, "it.unibo.sonardevice.Sonardevice").
 static(sonardevice).
  qactor( warningdevice, ctxmd, "it.unibo.warningdevice.Warningdevice").
 static(warningdevice).
