package it.unibo.wis.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * WISFacade - Spring Boot GUI per WasteIncineratorService
 * Versione semplificata con gestione errori migliorata
 */
@SpringBootApplication
@Controller
public class WISFacadeApp {

    private static final String GUIBRIDGE_URL = "http://localhost:8001/api/guibridge";

    public static void main(String[] args) {
        System.out.println("[WISFacade] Starting Spring Boot application...");
        SpringApplication.run(WISFacadeApp.class, args);
    }

    /**
     * Endpoint principale - dashboard
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        System.out.println("[WISFacade] Dashboard requested");
        
        SystemState state = fetchStateFromGUIBridge();
        
        model.addAttribute("incineratorState", state.incinerator);
        model.addAttribute("incineratorText", getIncineratorText(state.incinerator));
        model.addAttribute("incineratorClass", getIncineratorClass(state.incinerator));
        model.addAttribute("rpCount", state.rpCount);
        model.addAttribute("ashFull", state.ashFull);
        model.addAttribute("ashText", state.ashFull ? "FULL" : "Not Full");
        model.addAttribute("ashClass", state.ashFull ? "status-full" : "status-ok");
        model.addAttribute("robotX", state.robotX);
        model.addAttribute("robotY", state.robotY);
        model.addAttribute("robotJob", state.robotJob);
        model.addAttribute("timestamp", new Date());
        model.addAttribute("connected", state.connected);
        
        return "dashboard";
    }

    /**
     * Recupera stato da GUIBridge - CON TIMEOUT E GESTIONE ERRORI
     */
    private SystemState fetchStateFromGUIBridge() {
        SystemState state = new SystemState();
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            String response = restTemplate.getForObject(GUIBRIDGE_URL, String.class);
            
            if (response != null && !response.isEmpty()) {
                state = parseState(response);
                state.connected = true;
                System.out.println("[WISFacade] GUIBridge OK: " + response);
            }
            
        } catch (ResourceAccessException e) {
            // Timeout o connessione rifiutata
            System.err.println("[WISFacade] GUIBridge not reachable: " + e.getMessage());
            state.connected = false;
        } catch (Exception e) {
            // Altro errore
            System.err.println("[WISFacade] Error: " + e.getMessage());
            state.connected = false;
        }
        
        return state;
    }

    /**
     * Parse stringa stato
     */
    private SystemState parseState(String stateString) {
        SystemState state = new SystemState();
        
        if (stateString == null || stateString.isEmpty()) {
            return state;
        }
        
        try {
            Map<String, String> data = new HashMap<>();
            String[] pairs = stateString.split(",");
            
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    data.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
            
            state.incinerator = Integer.parseInt(data.getOrDefault("incinerator", "2"));
            state.rpCount = Integer.parseInt(data.getOrDefault("rp", "0"));
            state.ashFull = Integer.parseInt(data.getOrDefault("ash", "0")) == 1;
            state.robotX = Integer.parseInt(data.getOrDefault("x", "0"));
            state.robotY = Integer.parseInt(data.getOrDefault("y", "0"));
            state.robotJob = data.getOrDefault("job", "Unknown");
            
        } catch (Exception e) {
            System.err.println("[WISFacade] Parse error: " + e.getMessage());
        }
        
        return state;
    }

    private String getIncineratorText(int state) {
        switch (state) {
            case 0: return "Off";
            case 1: return "On (Burning)";
            case 2: return "Idle";
            default: return "Unknown";
        }
    }

    private String getIncineratorClass(int state) {
        switch (state) {
            case 0: return "status-off";
            case 1: return "status-on";
            case 2: return "status-idle";
            default: return "status-unknown";
        }
    }

    /**
     * Classe stato sistema
     */
    static class SystemState {
        int incinerator = 2;
        int rpCount = 0;
        boolean ashFull = false;
        int robotX = 0;
        int robotY = 0;
        String robotJob = "Waiting Home";
        boolean connected = false;  // NEW: indica se connesso a GUIBridge
    }
}
