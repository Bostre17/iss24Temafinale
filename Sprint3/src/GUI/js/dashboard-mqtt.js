/**
 * ============================================================
 * Waste Incinerator Monitoring System - Dashboard MQTT
 * Sprint 3 - MQTT Edition
 * ============================================================
 * 
 * Gestisce la connessione MQTT e l'aggiornamento real-time
 * della dashboard tramite sottoscrizione al topic 'sprint3'
 * 
 * @authors Bostrenghi Matteo & Severini Lorenzo
 * @version Sprint 3 - 1.0.0
 */

// ============================================================
// MQTT Configuration
// ============================================================
const MQTT_CONFIG = {
    // Broker WebSocket - usa porta 8080 per test.mosquitto.org
    broker: 'ws://test.mosquitto.org:8080',
    // Alternative se problemi con test.mosquitto.org:
    // broker: 'ws://broker.hivemq.com:8000',
    // broker: 'ws://mqtt.eclipseprojects.io:80/mqtt',
    
    topic: 'sprint3',
    
    // ClientID UNICO con timestamp per evitare conflitti
    clientId: 'wis-gui-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9),
    
    options: {
        clean: true,                    // Pulisce sessione precedente
        reconnectPeriod: 5000,          // Riconnetti dopo 5 secondi
        connectTimeout: 30 * 1000,      // Timeout connessione 30s
        keepalive: 60,                  // ⭐ Keepalive ogni 60 secondi
        protocolVersion: 4,             // ⭐ MQTT 3.1.1 (più compatibile)
        resubscribe: true               // ⭐ Ri-sottoscrivi automaticamente
    }
};

// ============================================================
// Global Variables
// ============================================================
var mqttClient = null;
var connected = false;
var logPaused = false;
var connectionStartTime = null;
var connectionTimer = null;

// Statistics
var stats = {
    messagesReceived: 0,
    eventsProcessed: 0,
    errors: 0
};

// System State
var systemState = {
    incinerator: 'IDLE',
    wasteStorageRP: 0,
    wasteStorageWeight: 0,
    ashStorageFull: false,
    ashStorageDistance: 0,
    ledStatus: 'OFF',
    robotPosition: { x: 0, y: 0 },
    robotJob: 'Waiting at Home',
    lastBurnTime: 0
};

// Map Positions (from sprint3.qak)
const positions = {
    home: { x: 0, y: 0, label: 'HOME', color: '#3498db' },
    burnin: { x: 2, y: 1, label: 'BURN IN', color: '#e74c3c' },
    burnout: { x: 4, y: 3, label: 'BURN OUT', color: '#95a5a6' },
    ashout: { x: 5, y: 4, label: 'ASH OUT', color: '#7f8c8d' },
    wastein: { x: 0, y: 4, label: 'WASTE IN', color: '#2ecc71' }
};

// ============================================================
// Document Ready
// ============================================================
$(document).ready(function() {
    console.log('Dashboard initialized - MQTT Edition');
    
    // Setup button handlers
    $('#connectBtn').click(connectMQTT);
    $('#disconnectBtn').click(disconnectMQTT);
    $('#clearLogBtn').click(clearLog);
    $('#pauseLogBtn').click(toggleLogPause);
    
    // Initialize map
    drawMap();
    
    // ⭐ MODIFICATO: Auto-connect dopo 2 secondi (più tempo per inizializzazione)
    // Commenta questa riga se vuoi connettere manualmente
    setTimeout(function() {
        addLog('Auto-connecting to MQTT broker...', 'info');
        connectMQTT();
    }, 2000);
    
    addLog('Dashboard loaded. Click "Connect" to start monitoring...', 'info');
});

// ============================================================
// MQTT Connection Management
// ============================================================

/**
 * Connect to MQTT broker
 */
function connectMQTT() {
    if (mqttClient && connected) {
        console.log('Already connected to MQTT broker');
        addLog('Already connected to MQTT broker', 'warning');
        return;
    }
    
    addLog('Connecting to MQTT broker: ' + MQTT_CONFIG.broker, 'mqtt');
    updateConnectionUI(false, true); // Connecting state
    
    try {
        addLog('Client ID: ' + MQTT_CONFIG.clientId, 'mqtt');
        
        // Create MQTT client
        mqttClient = mqtt.connect(MQTT_CONFIG.broker, {
            ...MQTT_CONFIG.options,
            clientId: MQTT_CONFIG.clientId
        });
        
        // Connection successful
        mqttClient.on('connect', function(connack) {
            connected = true;
            connectionStartTime = Date.now();
            updateConnectionUI(true, false);
            
            addLog('✓ Connected to MQTT broker successfully!', 'success');
            addLog('Session present: ' + (connack.sessionPresent ? 'yes' : 'no'), 'mqtt');
            
            // Subscribe to topic SOLO se non già sottoscritto
            if (!connack.sessionPresent) {
                mqttClient.subscribe(MQTT_CONFIG.topic, { qos: 1 }, function(err, granted) {
                    if (err) {
                        addLog('✗ Subscription error: ' + err.message, 'error');
                        stats.errors++;
                    } else {
                        addLog('✓ Subscribed to topic: ' + MQTT_CONFIG.topic + ' (QoS: ' + granted[0].qos + ')', 'success');
                        startConnectionTimer();
                    }
                });
            } else {
                addLog('✓ Already subscribed (session restored)', 'success');
                startConnectionTimer();
            }
        });
        
        // Message received
        mqttClient.on('message', function(topic, payload, packet) {
            stats.messagesReceived++;
            updateStatistics();
            
            const message = payload.toString();
            console.log('MQTT ←', message);
            
            handleMQTTMessage(message);
        });
        
        // Connection closed
        mqttClient.on('close', function() {
            connected = false;
            updateConnectionUI(false, false);
            stopConnectionTimer();
            addLog('✗ Connection closed', 'warning');
        });
        
        // Connection error
        mqttClient.on('error', function(error) {
            stats.errors++;
            updateStatistics();
            addLog('✗ MQTT error: ' + error.message, 'error');
            console.error('MQTT error:', error);
        });
        
        // Reconnecting
        mqttClient.on('reconnect', function() {
            addLog('⟳ Attempting to reconnect...', 'warning');
        });
        
        // Offline
        mqttClient.on('offline', function() {
            connected = false;
            updateConnectionUI(false, false);
            stopConnectionTimer();
            addLog('✗ Client went offline', 'warning');
        });
        
        // ⭐ NUOVO: Disconnect forzato dal broker
        mqttClient.on('disconnect', function(packet) {
            addLog('✗ Broker sent DISCONNECT (reason: ' + (packet.reasonCode || 'unknown') + ')', 'error');
            console.error('Disconnect packet:', packet);
        });
        
        // ⭐ NUOVO: Pacchetti inviati (debug)
        mqttClient.on('packetsend', function(packet) {
            if (packet.cmd === 'pingreq') {
                console.log('MQTT: Ping sent (keepalive)');
            }
        });
        
        // ⭐ NUOVO: Pacchetti ricevuti (debug)
        mqttClient.on('packetreceive', function(packet) {
            if (packet.cmd === 'pingresp') {
                console.log('MQTT: Ping response received');
            }
        });
        
    } catch (error) {
        addLog('✗ Failed to create MQTT client: ' + error.message, 'error');
        console.error('MQTT connection error:', error);
        updateConnectionUI(false, false);
    }
}

/**
 * Disconnect from MQTT broker
 */
function disconnectMQTT() {
    if (mqttClient && connected) {
        addLog('Disconnecting from MQTT broker...', 'info');
        mqttClient.end(false, {}, function() {
            addLog('✓ Disconnected successfully', 'info');
        });
        stopConnectionTimer();
    }
}

/**
 * Update connection UI state
 */
function updateConnectionUI(isConnected, isConnecting) {
    connected = isConnected;
    
    $('#connectBtn').prop('disabled', isConnected || isConnecting);
    $('#disconnectBtn').prop('disabled', !isConnected);
    
    if (isConnecting) {
        $('#mqttStatus').removeClass('bg-secondary bg-danger bg-success')
                       .addClass('bg-warning')
                       .html('<span class="loading"></span> Connecting...');
    } else if (isConnected) {
        $('#mqttStatus').removeClass('bg-secondary bg-danger bg-warning')
                       .addClass('bg-success')
                       .text('✓ Connected');
    } else {
        $('#mqttStatus').removeClass('bg-success bg-warning')
                       .addClass('bg-danger')
                       .text('✗ Disconnected');
    }
}

// ============================================================
// MQTT Message Handling
// ============================================================

/**
 * Parse and handle MQTT messages from QAK system
 */
function handleMQTTMessage(message) {
    try {
        // Nuovo formato QAK: msg(MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM)
        // Esempio: msg(stateScale,event,scale,none,stateScale(100),26)
        
        // Regex spiegata:
        // 1. (\w+) -> MSGID (es. stateScale)
        // 2. (\w+) -> MSGTYPE (es. event)
        // 3. (\w+) -> SENDER/Emitter (es. scale)
        // 4. (\w+) -> RECEIVER (es. none)
        // 5. (\w+) -> Nome del contenuto (es. stateScale)
        // 6. ([^)]*) -> Il valore dentro le parentesi (es. 100 o 0)
        // 7. (\d+) -> Numero di sequenza (es. 26)
        const qakMatch = message.match(/msg\((\w+),(\w+),(\w+),(\w+),(\w+)\(([^)]*)\),(\d+)\)/);
        
        if (qakMatch) {
            const eventType = qakMatch[1]; // stateScale o stateSonar
            const msgType   = qakMatch[2]; // event
            const emitter   = qakMatch[3]; // scale o monitoringdevice
            const payload   = qakMatch[6]; // il valore (100, 0, ecc.)
            
            console.log('Parsed QAK message:', { eventType, emitter, payload });
            
            stats.eventsProcessed++;
            updateStatistics();
            
            // Routing basato su eventType (MSGID)
            switch (eventType) {
                case 'stateScale':
                    handleScaleUpdate(payload, emitter);
                    break;
                    
                case 'stateSonar':
                    handleSonarUpdate(payload, emitter);
                    break;
                case 'incIdle':
                    // Inceneritore libero -> LED OFF
                    updateIncineratorStatus('IDLE', 'success');
                    updateLedStatus('OFF', 'secondary'); 
                    addLog('Incinerator is IDLE (LED OFF)', 'info');
                    break;
                case 'incBurn':
                    // Inceneritore in funzione -> LED ON
                    updateIncineratorStatus('BURNING', 'danger');
                    updateLedStatus('ON (Blinking)', 'warning');
                    addLog('Incinerator is BURNING (LED Blinking)', 'warning');
                    break;
                case 'burnEnd':
                    handleBurnEnd(payload, emitter);
                    break;
                    
                default:
                    addLog('Unknown MSGID: ' + eventType + ' from ' + emitter, 'warning');
            }
        } else {
            // Se non è un messaggio QAK standard, logga come testo semplice
            addLog('MQTT: ' + message, 'mqtt');
        }
        
    } catch (error) {
        console.error('Error parsing MQTT message:', error);
        addLog('Parse error: ' + error.message, 'error');
        stats.errors++;
        updateStatistics();
    }
}

// ============================================================
// System State Handlers
// ============================================================

/**
 * Handle scale (weight) update
 */
function handleScaleUpdate(payload, emitter) {
    const weight = parseInt(payload);
    
    if (isNaN(weight)) {
        addLog('Invalid scale weight: ' + payload, 'error');
        return;
    }
    
    systemState.wasteStorageWeight = weight;
    systemState.wasteStorageRP = Math.floor(weight / 50);
    
    $('#wasteStorageWeight').text(weight);
    $('#wasteStorageRP').text(systemState.wasteStorageRP);
    
    addLog(`📦 Scale update: ${weight} kg → ${systemState.wasteStorageRP} RP available`, 'info');
    
    // Visual feedback
    if (systemState.wasteStorageRP > 0) {
        $('#wasteStorageRP').addClass('text-success').removeClass('text-muted');
    } else {
        $('#wasteStorageRP').addClass('text-muted').removeClass('text-success');
    }
}

/**
 * Handle sonar (ash storage) update
 */
function handleSonarUpdate(payload, emitter) {
    const full = parseInt(payload);
    
    if (full !== 0 && full !== 1) {
        addLog('Invalid sonar state: ' + payload, 'error');
        return;
    }
    
    systemState.ashStorageFull = (full === 1);
    
    const status = systemState.ashStorageFull ? 'FULL' : 'EMPTY';
    const distance = systemState.ashStorageFull ? '≤ 40' : '> 40';
    
    $('#ashStorageStatus').text(status)
                         .removeClass('EMPTY FULL')
                         .addClass(status);
    $('#ashStorageDistance').text(distance);
    
    const logLevel = systemState.ashStorageFull ? 'warning' : 'success';
    addLog(`🗑️ Ash Storage: ${status} (distance ${distance} cm)`, logLevel);
    
    // Update LED based on ash storage and incinerator state
    updateLEDState();
}

function updateIncineratorStatus(status, type) {
    const el = document.getElementById('incinerator-status');
    if (el) {
        el.innerText = status;
        el.className = 'badge bg-' + type; // Cambia colore (es. bg-success, bg-danger)
    }
}

function updateLedStatus(status, type) {
    const el = document.getElementById('led-status');
    if (el) {
        el.innerText = status;
        // Se hai un'icona o un cerchio colorato per il LED:
        el.className = 'led-indicator led-' + type; 
    }
}

/**
 * Handle burn end event
 */
function handleBurnEnd(payload, emitter) {
    const burnTime = parseInt(payload);
    
    systemState.incinerator = 'IDLE';
    systemState.lastBurnTime = burnTime;
    
    $('#incineratorStatus').text('IDLE')
                          .removeClass('IDLE BURNING OFF')
                          .addClass('IDLE');
    
    $('#burnProgress').css('width', '0%');
    
    addLog(`🔥 Burn cycle completed in ${burnTime} seconds`, 'success');
    
    // Update LED
    updateLEDState();
}

/**
 * Update LED state based on system conditions
 */
function updateLEDState() {
    let ledState = 'OFF';
    
    // Logic from WIS (sprint3.qak lines 364-382)
    if (systemState.ashStorageFull && systemState.incinerator === 'IDLE') {
        ledState = 'BLINK';
    } else if (systemState.incinerator === 'BURNING') {
        ledState = 'ON';
    } else {
        ledState = 'OFF';
    }
    
    systemState.ledStatus = ledState;
    updateLED(ledState);
}

/**
 * Update LED visual indicator
 */
function updateLED(status) {
    const led = $('#ledIndicator');
    led.removeClass('led-off led-on led-blink');
    
    switch(status.toUpperCase()) {
        case 'ON':
            led.addClass('led-on');
            break;
        case 'BLINK':
            led.addClass('led-blink');
            break;
        default:
            led.addClass('led-off');
    }
    
    $('#ledStatus').text(status.toUpperCase());
}

// ============================================================
// Map Visualization
// ============================================================

/**
 * Draw system map on canvas
 */
function drawMap() {
    const canvas = document.getElementById('mapCanvas');
    if (!canvas || !canvas.getContext) return;
    
    const ctx = canvas.getContext('2d');
    const gridCols = 6;
    const gridRows = 5;
    const cellWidth = canvas.width / gridCols;
    const cellHeight = canvas.height / gridRows;
    
    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // Draw grid
    ctx.strokeStyle = '#e0e0e0';
    ctx.lineWidth = 1;
    
    for (let x = 0; x <= gridCols; x++) {
        ctx.beginPath();
        ctx.moveTo(x * cellWidth, 0);
        ctx.lineTo(x * cellWidth, canvas.height);
        ctx.stroke();
    }
    
    for (let y = 0; y <= gridRows; y++) {
        ctx.beginPath();
        ctx.moveTo(0, y * cellHeight);
        ctx.lineTo(canvas.width, y * cellHeight);
        ctx.stroke();
    }
    
    // Draw positions
    for (const [key, pos] of Object.entries(positions)) {
        const centerX = pos.x * cellWidth + cellWidth / 2;
        const centerY = pos.y * cellHeight + cellHeight / 2;
        
        // Draw circle
        ctx.fillStyle = pos.color;
        ctx.beginPath();
        ctx.arc(centerX, centerY, 25, 0, 2 * Math.PI);
        ctx.fill();
        
        // Draw border
        ctx.strokeStyle = '#2c3e50';
        ctx.lineWidth = 2;
        ctx.stroke();
        
        // Draw label
        ctx.fillStyle = '#2c3e50';
        ctx.font = 'bold 12px Arial';
        ctx.textAlign = 'center';
        ctx.fillText(pos.label, centerX, centerY - 35);
    }
    
    // Draw robot
    const robotX = systemState.robotPosition.x * cellWidth + cellWidth / 2;
    const robotY = systemState.robotPosition.y * cellHeight + cellHeight / 2;
    
    // Robot shadow
    ctx.fillStyle = 'rgba(0, 0, 0, 0.2)';
    ctx.beginPath();
    ctx.arc(robotX + 2, robotY + 2, 18, 0, 2 * Math.PI);
    ctx.fill();
    
    // Robot body
    ctx.fillStyle = '#3498db';
    ctx.beginPath();
    ctx.arc(robotX, robotY, 18, 0, 2 * Math.PI);
    ctx.fill();
    
    // Robot border
    ctx.strokeStyle = '#2980b9';
    ctx.lineWidth = 3;
    ctx.stroke();
    
    // Robot icon (emoji)
    ctx.font = 'bold 24px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText('🤖', robotX, robotY);
}

// ============================================================
// Statistics and UI Updates
// ============================================================

/**
 * Update MQTT statistics display
 */
function updateStatistics() {
    $('#mqttMessagesReceived').text(stats.messagesReceived);
    $('#mqttEventsProcessed').text(stats.eventsProcessed);
}

/**
 * Update connection time display
 */
function updateConnectionTime() {
    if (connectionStartTime) {
        const elapsed = Math.floor((Date.now() - connectionStartTime) / 1000);
        const minutes = Math.floor(elapsed / 60);
        const seconds = elapsed % 60;
        $('#mqttConnectionTime').text(
            String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0')
        );
    }
}

/**
 * Start connection timer
 */
function startConnectionTimer() {
    connectionTimer = setInterval(updateConnectionTime, 1000);
}

/**
 * Stop connection timer
 */
function stopConnectionTimer() {
    if (connectionTimer) {
        clearInterval(connectionTimer);
        connectionTimer = null;
        $('#mqttConnectionTime').text('00:00');
    }
}

// ============================================================
// Event Log Management
// ============================================================

/**
 * Add entry to event log
 */
function addLog(message, level = 'info') {
    if (logPaused) return;
    
    const timestamp = new Date().toLocaleTimeString();
    const logEntry = $('<div>')
        .addClass('log-entry log-' + level)
        .html(`<span class="log-timestamp">[${timestamp}]</span> ${message}`);
    
    const logContainer = $('#eventLog');
    
    // Remove placeholder if present
    if (logContainer.find('p.text-muted').length > 0) {
        logContainer.empty();
    }
    
    logContainer.append(logEntry);
    
    // Auto-scroll to bottom
    logContainer.scrollTop(logContainer[0].scrollHeight);
    
    // Limit log entries to last 150
    const entries = logContainer.find('.log-entry');
    if (entries.length > 150) {
        entries.first().remove();
    }
}

/**
 * Clear event log
 */
function clearLog() {
    $('#eventLog').html('<p class="text-muted p-3">Log cleared by user...</p>');
    addLog('Event log cleared', 'info');
}

/**
 * Toggle log pause
 */
function toggleLogPause() {
    logPaused = !logPaused;
    
    if (logPaused) {
        $('#pauseLogIcon').text('▶️');
        $('#pauseLogBtn').addClass('btn-warning').removeClass('btn-outline-light');
        addLog('Log paused by user', 'warning');
    } else {
        $('#pauseLogIcon').text('⏸️');
        $('#pauseLogBtn').removeClass('btn-warning').addClass('btn-outline-light');
        addLog('Log resumed', 'info');
    }
}

// ============================================================
// Utility Functions
// ============================================================

/**
 * Format timestamp
 */
function formatTimestamp(date) {
    return date.toLocaleTimeString('it-IT', { 
        hour: '2-digit', 
        minute: '2-digit', 
        second: '2-digit' 
    });
}

/**
 * Simulate system update (for testing without QAK backend)
 */
function simulateUpdate() {
    const testEvents = [
        'event(stateScale, stateScale(150), scale)',
        'event(stateSonar, stateSonar(0), monitoringdevice)',
        'event(burnEnd, burnEnd(10), incinerator)'
    ];
    
    const randomEvent = testEvents[Math.floor(Math.random() * testEvents.length)];
    handleMQTTMessage(randomEvent);
}

// Expose for testing in console
window.simulateUpdate = simulateUpdate;
window.systemState = systemState;
window.stats = stats;

console.log('Dashboard MQTT module loaded successfully');