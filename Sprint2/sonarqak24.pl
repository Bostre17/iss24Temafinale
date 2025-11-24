%====================================================================================
% sonarqak24 description   
%====================================================================================
event( sonardata, distance(D) ).
event( updateAS, updateAS(D) ).
dispatch( sonarstart, sonarstart(X) ).
dispatch( sonarwork, sonarwork(X) ).
dispatch( sonarstop, sonarstop(X) ).
dispatch( doread, doread(X) ).
%====================================================================================
context(ctxsonarqak24, "localhost",  "TCP", "8128").
context(ctxwis, "192.168.1.27",  "TCP", "8001").
 qactor( wis, ctxwis, "external").
  qactor( sonar24, ctxsonarqak24, "it.unibo.sonar24.Sonar24").
 static(sonar24).
  qactor( datacleaner, ctxsonarqak24, "it.unibo.datacleaner.Datacleaner").
 static(datacleaner).
  qactor( sonardevice, ctxsonarqak24, "it.unibo.sonardevice.Sonardevice").
 static(sonardevice).
