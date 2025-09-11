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
					e.printStackTrace();
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
					// Il comando esegue il sistema definito in analisirequisiti.qak
					CommUtils.outred("TestWIS activateWIS " );
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
		if (connSupport == null) {
			CommUtils.outmagenta("Connecting to the system...");
			connSupport = ConnectionFactory.createClientSupport(ProtocolType.tcp, "localhost", "8001");
			CommUtils.outcyan("CONNECTED to wis " + connSupport);
		}
	}

	@Test
	public void testSuccessfulRun() {
		IApplMessage reqScale  = CommUtils.buildDispatch("tester", "stateScale", "stateScale(100)", "wis");
		IApplMessage reqSonar  = CommUtils.buildDispatch("tester", "stateSonar", "stateSonar(10)", "wis");
		try {
			// Attende che il sistema sia pronto
			Thread.sleep(5000); 
			connect();
			CommUtils.outmagenta("testSuccessfulRun =======================================");
			
			// 1. Simula la presenza di rifiuti (peso > 0)
			connSupport.forward(reqScale);
			CommUtils.outcyan("Sent stateScale message");
			Thread.sleep(1000);
			
			// 2. Simula che ci sia spazio per le ceneri (distanza > DLIMT)
			connSupport.forward(reqSonar);
			CommUtils.outcyan("Sent stateSonar message");
			
			// 3. Si attende che il ciclo si completi (un tempo ragionevolmente lungo)
			// Questo test è di osservazione: si verifica che il sistema esegua la routine completa
			// Per un test automatico servirebbe un messaggio di completamento dal WIS.
			CommUtils.outmagenta("Test is observational: checking logs for correct execution flow...");
			Thread.sleep(25000); // Tempo per permettere al robot di completare il ciclo
			
		} catch (Exception e) {
			CommUtils.outred("testSuccessfulRun ERROR " + e.getMessage());
		}
	}

	@Test
	public void testAshStorageFull() {
		// Invia un valore di sonar che è sotto la soglia DLIMT (5)
		IApplMessage reqScale  = CommUtils.buildDispatch("tester", "stateScale", "stateScale(100)", "wis");
		IApplMessage reqSonar  = CommUtils.buildDispatch("tester", "stateSonar", "stateSonar(4)", "wis");
		
		try {
			Thread.sleep(5000);
			connect();
			CommUtils.outmagenta("testAshStorageFull =======================================");
			
			// 1. Simula la presenza di rifiuti
			connSupport.forward(reqScale);
			CommUtils.outcyan("Sent stateScale message");
			Thread.sleep(1000);
			
			// 2. Simula che lo storage delle ceneri sia pieno
			connSupport.forward(reqSonar);
			CommUtils.outcyan("Sent stateSonar message (Ash storage is full)");
			
			// In questo caso, il sistema dovrebbe rimanere nello stato 'waitingRP'
			// e non avviare la routine. Il test è osservazionale.
			CommUtils.outmagenta("Test is observational: WIS should not start the routine.");
			Thread.sleep(10000); 

		} catch (Exception e) {
			CommUtils.outred("testAshStorageFull ERROR " + e.getMessage());
			fail("testRequest " + e.getMessage());
		}
	}
}