//package test.java;
// 
//import static org.junit.Assert.fail;
//import org.junit.After;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import unibo.basicomm23.interfaces.IApplMessage;
//import unibo.basicomm23.interfaces.Interaction;
//import unibo.basicomm23.msg.ApplMessage;
//import unibo.basicomm23.msg.ProtocolType;
//import unibo.basicomm23.utils.ColorsOut;
//import unibo.basicomm23.utils.CommUtils;
//import unibo.basicomm23.utils.ConnectionFactory;
//
//
///*
// * ===========================================================================
// * TestMonitoringDeviceLogicTCPOnly
// * Verifica la logica del monitoringdevice usando solo connessioni TCP: 
// * invio di sonarstart/sonarstop e simulazione di eventi sonardata.
// * NOTA: Non potendo intercettare eventi (stateSonar) via TCP, 
// * le verifiche si basano sull'input e sull'osservazione del log.
// * ===========================================================================
// */
//public class TestSonar24 {
//	private static Interaction connTcpSupport;
//	
//	// Dati di connessione TCP per inviare Dispatch e simulare Eventi (ctxmd:8128)
//	private static final String HOST = "localhost";
//	private static final String PORT_TCP = "8128"; 
//	private static final String ACTOR_NAME = "monitoringdevice";
//	private static final String SENDER = "testClient";
//    private static final int DLIMT = 20; // Distanza Limite (usata per impostare la soglia)
//
//    
//
//	@BeforeClass
//	public static void activate() {
//		CommUtils.outmagenta("TestMonitoringDeviceLogicTCPOnly activate ");
//	}
//
//	@After
//	public void down() {
//		CommUtils.outmagenta("end of test ");
//	}
//    
//    /**
//     * Helper per connettersi via TCP (Dispatch/Forward/Event Simulation)
//     */
//    private Interaction connectTcp() throws Exception {
//        if (connTcpSupport == null) {
//            Thread.sleep(1000); // Attende l'avvio del sistema
//            while (connTcpSupport == null) {
//                connTcpSupport = ConnectionFactory.createClientSupport(
//                        ProtocolType.tcp, HOST, PORT_TCP // Crea connessione TCP
//                );
//                CommUtils.outmagenta("Test connect TCP attempt to " + PORT_TCP);
//                Thread.sleep(200);
//            }
//            CommUtils.outmagenta("CONNECTED TCP to ctxmd (" + PORT_TCP + ").");
//        }
//        return connTcpSupport;
//    }
//    
//    /**
//     * Helper per simulare l'evento 'sonardata: distance(D)' inviato dal datacleaner
//     */
//    private void sendSimulatedSonardata(Interaction conn, int distance) throws Exception {
//        // Formato Evento: event(MSGID, MSGC, SENDER, RECEIVER)
//        String msgStr = String.format("event(sonardata, distance(%d), datacleaner, %s)", distance, ACTOR_NAME);
//        IApplMessage event = new ApplMessage(msgStr);
//        conn.forward(event); // Invia l'evento tramite la connessione TCP al contesto Qak
//    }
//
//
//	@Test(timeout=15000)
//	public void testAshStorageLogic() {
//		
//		try {
//            // 1. Setup connessione
// 			connTcpSupport = connectTcp();
//
//            // 2. Inviare sonarstart per impostare DLIMT=20 (s0 -> initializing)
//            IApplMessage startReq  = CommUtils.buildDispatch( 
//                SENDER, 
//                "sonarstart", 
//                "sonarstart(" + DLIMT + ")", // Payload: DLIMT=20
//                ACTOR_NAME
//            );
//            connTcpSupport.forward(startReq);
//            CommUtils.outmagenta("Sent sonarstart with DLIMT=" + DLIMT + ". Verificare il log per l'avvio del sonardevice.");
//            Thread.sleep(1000); // Tempo per l'attore Qak per transire a handlesonardata
//
//            
//            // --- SCENARIO 1: Ash Storage PIENO (DISTANCE <= DLIMT) ---
//            int distanceFull = DLIMT - 5; // Es. 15 <= 20 (Soglia superata)
//