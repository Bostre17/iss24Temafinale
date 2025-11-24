/*package test.java;
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.ColorsOut;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;
import unibo.basicomm23.actors.ActorBasic;
import unibo.basicomm23.actors.context.ContextUtils;

/*
 * ===========================================================================
 * TestSonar24
 * ===========================================================================
FINE COMMENTO QUA

public class TestSonar24 {
private static Interaction connSupport;
private static Process p;
private static final String HOST = "localhost";
private static final String PORT = "8128"; // Porta del ctxsonarqak24
private static final int DLIMT = 20; // 20 cm come Distanza Limite per il test

// Attore fittizio per intercettare gli eventi stateSonar emessi da sonar24
public static class EventReceiver extends ActorBasic {
    private String lastState = "UNKNOWN";

    public EventReceiver(String name, String ctxName) {
        super(name, ContextUtils.get
        (ctxName));
        // Si sottoscrive all'evento stateSonar di sonar24
        ContextUtils.get(ctxName).addListener(this, "stateSonar"); 
    }

    @Override
    protected void doJob(IApplMessage msg) throws Exception {
        if (msg.msgType().equals("event") && msg.msgId().equals("stateSonar")) {
            // Il payload è stateSonar(FULL), dove FULL è un booleano
            String content = msg.msgContent();
            lastState = content.substring(content.indexOf("(") + 1, content.indexOf(")"));
            ColorsOut.outappl("EventReceiver received stateSonar: " + lastState, ColorsOut.MAGENTA);
        }
    }

    public String getLastState() {
        return lastState;
    }
}

private static EventReceiver receiver;

	@BeforeClass
	public static void activate() {
		CommUtils.outmagenta("TestSonar24 activate ");
        // Avvia il sistema Qak contenente sonar24 nel contesto ctxsonarqak24
 		// In un ambiente reale, attiveresti il sistema con Gradle o Dist
        // activateSystemUsingGradle(); 
        CommUtils.outmagenta("Assuming sonar24 system is running on " + HOST + ":" + PORT);
        
        // Crea l'EventReceiver nello stesso contesto di sonar24 per intercettare stateSonar
        receiver = new EventReceiver("testReceiver", "ctxsonarqak24");
        receiver.activate();
	}
	
	@After
	public void down() { 
		CommUtils.outmagenta("end of test ");
        // Codice per killare il processo se necessario
	}

    private void sendSonarDataEvent(int distance) throws Exception {
        // Simula l'evento 'sonardata: distance(D)' emesso da datacleaner
        String msgStr = String.format("event(sonardata, sonardata(%d), datacleaner, sonar24)", distance);
        IApplMessage event = new ApplMessage(msgStr);
        // Invia l'evento fittizio direttamente a sonar24 (tramite il contesto)
        connSupport.forward(event); 
        CommUtils.outappl("--- Sent sonardata: distance(" + distance + ") ---", ColorsOut.YELLOW);
    }
 
	@Test
	public void testAshStorageTransitions() {
		
		try {
            // 1. Connessione al contesto di sonar24
 			Thread.sleep(2000);
            while( connSupport == null ) {
 				connSupport = ConnectionFactory.createClientSupport(ProtocolType.tcp, HOST, PORT);
 				CommUtils.outmagenta("Test connect attempt to " + PORT);
 				Thread.sleep(200);
 			}
            CommUtils.outmagenta("CONNECTED to sonar24 context.");
            
            // 2. Inviare sonarstart a sonar24 con DLIMT
            IApplMessage startReq  = CommUtils.buildDispatch( 
                "testUnit", 
                "sonarstart", 
                "sonarstart(" + DLIMT + ")", 
                "sonar24"
            );
            connSupport.forward(startReq);
            CommUtils.outmagenta("Sent sonarstart with DLIMT=" + DLIMT);
            Thread.sleep(1000); // Give time for initialization (s0 -> initializing)
            
            // --- SCENARIO 1: VUOTO -> VUOTO (handleDataEmpty) ---
            
            // Simula distanza > DLIMT (e.g., 30 > 20)
            sendSonarDataEvent(30); 
            Thread.sleep(500);
            // Non ci dovrebbe essere un stateSonar, sonar24 rimane in workAsEmpty 
            String initialState = receiver.getLastState();
            assertEquals("UNKNOWN", initialState); 
            
            // Simula altra distanza > DLIMT (e.g., 25 > 20)
            sendSonarDataEvent(25);
            Thread.sleep(500);
            assertEquals("UNKNOWN", receiver.getLastState()); 

            // --- SCENARIO 2: VUOTO -> PIENO (stopWis) ---
            
            // Simula distanza <= DLIMT (e.g., 15 <= 20)
            sendSonarDataEvent(15); 
            Thread.sleep(1000); // Wait for transition and event emission
            
            // Verifica che AshStorage sia diventato PIENO (FULL=true) 
            assertEquals("true", receiver.getLastState()); 
            CommUtils.outgreen("Ash Storage transition to FULL verified.");
            
            // --- SCENARIO 3: PIENO -> PIENO (handleDataFull) ---
            
            // Simula distanza <= DLIMT (e.g., 10 <= 20)
            sendSonarDataEvent(10); 
            Thread.sleep(500);
            // Non ci dovrebbe essere un stateSonar, sonar24 rimane in workAsFull 
            assertEquals("true", receiver.getLastState()); 

            // --- SCENARIO 4: PIENO -> VUOTO (startWis) ---
            
            // Simula distanza > DLIMT (e.g., 40 > 20)
            sendSonarDataEvent(40); 
            Thread.sleep(1000); // Wait for transition and event emission
            
            // Verifica che AshStorage sia diventato VUOTO (FULL=false) 
            assertEquals("false", receiver.getLastState()); 
            CommUtils.outgreen("Ash Storage transition to EMPTY verified.");


		} catch (Exception e) {
			CommUtils.outred("testAshStorageTransitions ERROR " + e.getMessage());
			e.printStackTrace();
			fail("testAshStorageTransitions failed: " + e.getMessage());
		}
	}
}*/