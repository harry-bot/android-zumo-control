#include <ZumoMotors.h>
#include <WiFiNINA.h>

#define WIFI_SSID "Phasix_2.4G"
#define WIFI_PASSWORD "ver1f1er"
#define LISTEN_PORT 8000

ZumoMotors motors;
WiFiServer server(LISTEN_PORT);

int leftMotorValue, rightMotorValue;

void setup() {
  Serial.begin(9600);
  Serial.println("Starting...");
  
  if (WiFi.status() == WL_NO_MODULE) {
    Serial.println("Communication with WiFi module failed!");
    // don't continue
    while (true);
  }

  String fv = WiFi.firmwareVersion();
  if (fv < WIFI_FIRMWARE_LATEST_VERSION) {
    Serial.println("Please upgrade the firmware");
  }

  int status = WL_IDLE_STATUS;
  // attempt to connect to Wifi network:
  while (status != WL_CONNECTED) {
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(WIFI_SSID);
    status = WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  }
  Serial.println("Connected to wifi");

  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your board's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");

  server.begin();
}

void loop() {  
  WiFiClient client = server.available();
  if (client) {
    debug("New client!");
    while (client.connected() && readClient(&client)) {
      debug("L:" + String(leftMotorValue) + "R:" + String(rightMotorValue));
      motors.setSpeeds(leftMotorValue * 4, rightMotorValue * 4);
    }
    debug("Client disconnected");
    motors.setSpeeds(0, 0);
    if (client.connected()) client.stop();
  }
}

void debug(String msg) {
  Serial.print(millis());
  Serial.print(" ");
  Serial.println(msg);
}

bool readClient(WiFiClient* client) {
  bool leftUpdated = false, rightUpdated = false;
  while (client->connected()) {
    unsigned int counter = 1;
    while (counter <= 100) {
      if (client->available()) break;
      if (counter == 100) {
        if (!client->connected()) return false;
        counter = 0;
      }
      counter++;
      delay(1);
    }
    char data = client->read();
    if (data == ';') {
      if (leftUpdated && rightUpdated) {
        return true;
      } else { // We received ; before setting both motors
        leftUpdated = false;
        rightUpdated = false;
      }
    } else if (!leftUpdated) { // First byte is left
      leftMotorValue = (int) data;
      leftUpdated = true;
    } else if (!rightUpdated) { // Second byte is right
      rightMotorValue = (int) data;
      rightUpdated = true;
    }
  }
  return false;
}
