%====================================================================================
% monitoringdevice description   
%====================================================================================
mqttBroker("test.mosquitto.org", "1883", "sprint3").
event( sonardevicedata, sonardevicedata(DISTANCE) ).
event( sonardatacleaner, sonardatacleaner(DISTANCE) ).
event( stateSonar, stateSonar(FULL) ).
dispatch( sonarstart, sonarstart(X) ).
dispatch( sonarwork, sonarwork(X) ).
dispatch( sonarstop, sonarstop(X) ).
dispatch( newAsh, newAsh(X) ).
dispatch( emptyAsh, emptyAsh(X) ).
dispatch( ledOn, ledOn(X) ).
dispatch( ledOff, ledOff(X) ).
dispatch( ledBlink, ledBlink(X) ).
dispatch( doread, doread(X) ).
%====================================================================================
context(ctxmd, "localhost",  "TCP", "8128").
context(ctxwis, "192.168.1.10",  "TCP", "8001").
 qactor( sonardevicemock, ctxmd, "it.unibo.sonardevicemock.Sonardevicemock").
 static(sonardevicemock).
  qactor( warningdevice, ctxmd, "it.unibo.warningdevice.Warningdevice").
 static(warningdevice).
  qactor( datacleaner, ctxmd, "it.unibo.datacleaner.Datacleaner").
 static(datacleaner).
  qactor( monitoringdevice, ctxmd, "it.unibo.monitoringdevice.Monitoringdevice").
 static(monitoringdevice).
