package main.java.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.ColorsOut;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;


public class WISSprint1Test {

	private static Interaction connSupport;
	private static Process procWIS;

	// Metodo di supporto per mostrare l'output dei messaggi a colori
	public static void showOutput(Process proc, String color){
		new Thread(){
			public void run(){
				try {
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
					ColorsOut.outappl("Here is the standard output of the command:\n", color);
					while (true){
						String s = stdInput.readLine();
						if ( s != null ) ColorsOut.outappl( s, color );
					}
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}.start();
	}

	// Metodo per attivare il sistema logico del WIS
	public static void activateWIS() {
		Thread th = new Thread(){
			public void run(){
				CommUtils.outmagenta("TestWIS activateWIS");
				try {
					procWIS = Runtime.getRuntime().exec("cmd /c gradlew.bat run");
					showOutput(procWIS, ColorsOut.GREEN);
				} catch ( Exception e) {
					CommUtils.outred("TestWIS activateWIS ERROR " + e.getMessage());
				}
			}
		};
		th.start();
	}

	@BeforeClass
	// Metodo per attivare il sistema complessivo, prima di partire con i veri test
	public static void activate() {
		CommUtils.outmagenta("TestWIS activate ");
		activateWIS();
	}

	@AfterClass
	// Metodo per disattivare tutto, una volta completati i vari test
	public static void down() {
		CommUtils.outmagenta("end of test ");
		if (procWIS != null) {
			procWIS.destroy();
		}
	}
	
	private void connect() throws Exception {
		// Aggiungiamo un ritardo per essere sicuri che il server sia in ascolto
		Thread.sleep(2000); 
		if (connSupport == null) {
			CommUtils.outmagenta("Connecting to the system...");
			connSupport = ConnectionFactory.createClientSupport(ProtocolType.tcp, "localhost", "8001");
			CommUtils.outcyan("CONNECTED to wis " + connSupport);
		}
	}

	@Test
	public void testSuccessfulRun() {
		try {
			
			CommUtils.outmagenta("testSuccessfulRun =======================================");
			CommUtils.outmagenta("Test is observational: waiting for the autonomous QAK system to run...");
			
			// Attendiamo abbastanza tempo per l'avvio dei mock e un ciclo completo
			// (10s delay + 10s burn + movimenti robot)
			Thread.sleep(30000); 
			
		} catch (Exception e) {
			CommUtils.outred("testSuccessfulRun ERROR " + e.getMessage());
		}
	}

	@Test
	public void testAshStorageFull() {
		
		// Il DLIMT è 5. Il sonarmock parte da 200. Ogni 'newAsh' toglie 60.
		// 200 -> 140 -> 80 -> 20 -> -40 (poi 0).
		// Servono 4 messaggi per scendere sotto 5.
		IApplMessage reqNewAsh  = CommUtils.buildDispatch("tester", "newAsh", "newAsh(1)", "sonarmock");
		
		try {
			// Attende che il sistema sia pronto e connesso
			connect();
			CommUtils.outmagenta("testAshStorageFull =======================================");
			
			CommUtils.outcyan("Forcing AshStorage to FULL state by sending 'newAsh' messages to sonarmock...");
			
			// Inviamo 4 messaggi 'newAsh' al sonarmock per simulare il riempimento
			connSupport.forward(reqNewAsh);
			Thread.sleep(100); // Piccola pausa tra i messaggi
			connSupport.forward(reqNewAsh);
			Thread.sleep(100);
			connSupport.forward(reqNewAsh);
			Thread.sleep(100);
			connSupport.forward(reqNewAsh);
			
			CommUtils.outcyan("Sent 4 'newAsh' messages. Ash level should now be 0 (less than DLIMT=5).");
			
			// Ora attendiamo. A T=10s, 'scalemock' invierà 'stateScale(100)'.
			// Il 'wis' controllerà le condizioni:
			// RP=2 (true), incinerator=2 (true), ashStorageLevel > DLIMT (0 > 5 -> false)
			// Quindi la routine NON dovrebbe partire.
			CommUtils.outmagenta("Test is observational: WIS should NOT start the routine.");
			Thread.sleep(15000); // Tempo sufficiente per osservare che non parte nulla

		} catch (Exception e) {
			CommUtils.outred("testAshStorageFull ERROR " + e.getMessage());
			fail("testRequest " + e.getMessage());
		}
	}
}