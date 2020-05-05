#include <ZumoMotors.h>
#include <SoftwareSerial.h>

ZumoMotors motors;

#define debug Serial
SoftwareSerial BT(A2, A3); // RX, TX
#define BT_VCC A0
#define BT_GND A1

int leftMotorValue, rightMotorValue;

void setup() {  
  BT.begin(38400);
  pinMode(BT_VCC, OUTPUT);
  pinMode(BT_GND, OUTPUT);
  digitalWrite(BT_VCC, HIGH);
  digitalWrite(BT_GND, LOW);
  
  debug.begin(112500);
  debug.println("Starting...");
}

void loop() {  
  readBT();
  debug.println("L:" + String(leftMotorValue) + "R:" + String(rightMotorValue));
  motors.setSpeeds(leftMotorValue * 4, rightMotorValue * 4);
}

void readBT() {
  bool leftUpdated = false, rightUpdated = false;
  while (1) {
    while (!BT.available());
    char data = BT.read();
    if (data == ';') {
      if (leftUpdated && rightUpdated) {
        return;
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
}


