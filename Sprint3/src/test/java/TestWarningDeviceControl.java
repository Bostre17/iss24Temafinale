/*package test.java;
 
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.ColorsOut;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;


/*
 * ===========================================================================
 * TestWarningDeviceControl 
 * ===========================================================================
 
public class TestWarningDeviceControl {
	private static Interaction connSupport;
	private static Process p;
	private static final String HOST = "localhost";
// Sostituisci con la porta del contesto in cui risiede il Warning Device (es. ctxmd)
	private static final String PORT = "8128";
	private static final String ACTOR_NAME = "warningdevice";

	@BeforeClass
	public static void activate() {
		CommUtils.outmagenta("TestWarningDeviceControl activate ");
	}

	@After
	public void down() {
		CommUtils.outmagenta("end of test ");
	}
	 
	@Test
	public void testLEDCommandsDispatch() {
		
		try {
 			// 1. Attendi l'avvio del contesto e stabilisci la connessione
 			Thread.sleep(2000); 
 			CommUtils.outmagenta("testLEDCommandsDispatch ======================================= ");
			
			// Connessione al contesto del Warning Device (ctxmd:8128)
			while( connSupport == null ) {
 				connSupport = ConnectionFactory.createClientSupport(
					ProtocolType.tcp, HOST, PORT
				);
 				CommUtils.outmagenta("Test connect attempt to " + PORT);
 				Thread.sleep(200);
 			}
			Thread.sleep(1000);
 			CommUtils.outmagenta("CONNECTED to ctxmd (" + PORT + ").");
			
            // Sequenza di comandi da testare
            String[] commands = {"ledOn", "ledBlink", "ledOff"};
            
            for (String cmd : commands) {
                
                // 2. Costruisci il Dispatch (il payload '1' è un placeholder)
                IApplMessage dispatchReq  = CommUtils.buildDispatch( 
                    "testUnit", 
                    cmd, 
                    cmd + "(1)", 
                    ACTOR_NAME
                );
                
                // 3. Invia il Dispatch
                CommUtils.outmagenta("Sending Dispatch: " + dispatchReq);
                connSupport.forward(dispatchReq);
                
                // 4. Attendi 1 secondo per permettere al Warning Device di ricevere 
                // ed eseguire l'azione (e stampare il nuovo stato in console).
                Thread.sleep(1000);
                CommUtils.outcyan(ACTOR_NAME + " dovrebbe essere nello stato: " + cmd);
            }
            
            CommUtils.outgreen("Tutti i comandi Dispatch inviati con successo al Warning Device.");
			
		} catch (Exception e) {
			CommUtils.outred("testLEDCommandsDispatch ERROR " + e.getMessage());
			e.printStackTrace();
			fail("testLEDCommandsDispatch failed: " + e.getMessage());
		}
	}
}*/