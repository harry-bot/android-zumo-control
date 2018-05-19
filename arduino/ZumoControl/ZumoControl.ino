#include <ZumoMotors.h>
#include <SoftwareSerial.h>

ZumoMotors motors;
SoftwareSerial softSerial(A2, A3); // RX, TX

String receivedString = "";

void setup() {
  softSerial.begin(9600);
  pinMode(A0, OUTPUT);
  pinMode(A1, OUTPUT);
  digitalWrite(A0, HIGH);
  digitalWrite(A1, LOW);
}

void loop() {  
  receivedString = readSoftSerial();
  if (receivedString.startsWith("set")) {
    ProcessSpeeds(receivedString);
  }
}  

String readSoftSerial() {
  if (softSerial.available()) {
    softSerial.setTimeout(5);
    return softSerial.readString();
  } else {
    return "";
  }
}

void ProcessSpeeds(String command) {
  command.toLowerCase();

  bool l = false, r = false;
  
  int commandPos = command.lastIndexOf("set");

  if (commandPos == -1) return;
  if (command.indexOf('l', commandPos) != -1) l = true;
  if (command.indexOf('r', commandPos) != -1) r = true;

  String lval, rval;

  char commandc[128];
  
  command.toCharArray(commandc, 128);

  if (l) {
    int firstl = command.indexOf('l', commandPos);
    int secondl = command.indexOf('l', firstl+1);
    char lvalue[64];
    int n = 0;
    
    if (secondl == -1) {Serial.println("ProcessSpeeds() syntax error for l"); return;}

    for (int i = firstl + 1; i < secondl; i++)
    {
      lvalue[n++] = commandc[i];
    }

    lvalue[n] = '\0';
    lval = String(lvalue);
  }

  if (r) {
    int firstr = command.indexOf('r', commandPos);
    int secondr = command.indexOf('r', firstr+1);
    char rvalue[64];
    int n = 0;
    
    if (secondr == -1) {Serial.println("ProcessSpeeds()) syntax error for r"); return;}

    for (int i = firstr + 1; i < secondr; i++)
    {
      rvalue[n++] = commandc[i];
    }

    rvalue[n] = '\0';
    rval = String(rvalue);
  }

  if (l && r) {
    int lValue = lval.toInt();
    int rValue = rval.toInt();

    lValue = lValue * 4;
    rValue = rValue * 4;

    motors.setSpeeds(lValue, rValue);
  }
}


