#include <ZumoMotors.h>
#include <ZumoBuzzer.h>
#include <Pushbutton.h>
#include <SoftwareSerial.h>

ZumoMotors motors;
ZumoBuzzer buzzer;
Pushbutton button(ZUMO_BUTTON);
SoftwareSerial softSerial(A2, A3); // RX, TX

String receivedString = "";

int NORMAL_SPEED = 200;
int TURN_SPEED = 100;
int SnormalSpeed = 400;
int SturnSpeed = 200;

String movement = "s";

#define LED 13

void setup()
{
  softSerial.begin(9600);   
  softSerial.println("Ready");
  softSerial.println("v2.0");
  pinMode(LED, OUTPUT);
  pinMode(A0, OUTPUT);
  pinMode(A1, OUTPUT);
  digitalWrite(A0, HIGH);
  digitalWrite(A1, LOW);
}

void loop()
{  
  receivedString = readsoftSerial(); // Read the softSerial

  if (receivedString == "bounce") softSerial.println("Boing!");
  
  if (receivedString == "f") forwardPress();
  
  if (receivedString == "b") backwardPress();
  
  if (receivedString == "l") leftPress();
  
  if (receivedString == "r") rightPress();
  
  if (receivedString == "s"){
    motors.setSpeeds(0,0);
    softSerial.println((String)millis() + ": S");
    movement = "s";
  }

  // and so on
  
  if (receivedString == "sf" || receivedString =="Sf")
  {
    sprintForwards();
  }
  
  if (receivedString == "sb" || receivedString == "Sb")
  {
    sprintBackwards();
  }
  
  if (receivedString == "sl" || receivedString == "Sl")
  {
    sprintLeft();
  }
  
  if (receivedString == "sr" || receivedString == "Sr")
  {
    sprintRight();
  }
  
  if (receivedString == "ssl" || receivedString == "Ssl")
  {
    superSprintLeft();
  }
  
  if (receivedString == "ssr" || receivedString == "Ssr")
  {
    superSprintRight();
  }
  
  if (receivedString == "ledon" || receivedString == "Ledon")
  {
    digitalWrite(LED, HIGH);
    softSerial.println((String)millis() + ": L1");
  }
  
  if (receivedString == "ledoff" || receivedString == "Ledoff")
  {
    digitalWrite(LED, LOW);
    softSerial.println((String)millis() + ": L0");
  }
  
  if (receivedString == "cr" || receivedString == "Cr")
  {
    curveRight();
    movement = "cr";
  }
  
  if (receivedString == "cl" || receivedString == "Cl")
  {
    curveLeft();
    movement = "cl";
  }
  
  if (receivedString == "help" || receivedString == "Help")
  {
    softSerial.println("***softSerial control for Zumo***");
    softSerial.println("f, b, l, r, s (stop) for basic control");
    softSerial.println("Add an S to make them sprint controls (apart from stop)");
    softSerial.println("cr = Curve right");
    softSerial.println("cl = Curve left");
    softSerial.println("ledon = Turn on LED");
    softSerial.println("ledoff = Turn off LED");
    softSerial.println("version = Version info and instructions");
    softSerial.println("Harry Soft , v1.0");
  }
  if (receivedString == "setspeeds" || receivedString == "Setspeeds")
  {
    softSerial.println("Setting Speeds...");
    int result = setSpeeds();
    if (result == 1)
    {
      softSerial.println("Motor setting cancelled");
    }
    if (result == 0)
    {
      softSerial.println("Motor setting successful");
    }
  }
  
  if (receivedString != "")
  {
    buzzer.play("L16 b"); // Play a beep to anknowledge the command
  }
}  

String readsoftSerial()
{
  char inData[256] = "";
  char inChar;
  byte index = 0;
  
  if (softSerial.available())
  { 
    delay(50);
    while(softSerial.available()) // Don't read unless you know there is data
    {
        inChar = softSerial.read(); // Read a charact er
        inData[index] = inChar; // Store it
        index++; // Increment where to write next 
    }
    return String(inData);
  }
  else
  {
    return "";
  }
}

int setSpeeds()
{
  int receivedNumber = 0;
  
  // Set normal motor speed
  receivedNumber = readNumber("Please enter a valid number for the normal speed that is more than 0. Enter cancel to exit");
  if (receivedNumber == 0)
  {
    return 1;
  }
  NORMAL_SPEED = receivedNumber;
  
  // Set turn motor speed
  receivedNumber = readNumber("Please enter a valid number for the turn speed that is more than 0. Enter cancel to exit");
  if (receivedNumber == 0)
  {
    return 1;
  }
  TURN_SPEED = receivedNumber;
  
  // Set normal sprint motor speed
  receivedNumber = readNumber("Please enter a valid number for the normal sprint speed that is more than 0. Enter cancel to exit");
  if (receivedNumber == 0)
  {
    return 1;
  }
  SnormalSpeed = receivedNumber;
  
  // Set turn sprint motor speed
  receivedNumber = readNumber("Please enter a valid number for the turn sprint speed that is more than 0. Enter cancel to exit");
  if (receivedNumber == 0)
  {
    return 1;
  }
  SturnSpeed = receivedNumber;
  
  return 0;
}

int readNumber(String toPrint)
{
  softSerial.println(toPrint);
  String tempReceivedString = "1";
  int tempReceivedNumber = 0;
  softSerial.println("If an invalid number is entered then nothing will happen. Enter a valid number and the process will continue.");
  while(1)
  {
    tempReceivedString = readsoftSerial();
    if (tempReceivedString == "cancel")
    {
      return 0;
    }
    tempReceivedNumber = tempReceivedString.toInt();
    if (tempReceivedNumber > 0)
    {
      return tempReceivedNumber;
    }
  }
}

//
// Commands section - commands used throughout the program to drive the zumo
//

void forwards()
{
  motors.setSpeeds(NORMAL_SPEED, NORMAL_SPEED);
  softSerial.println((String)millis() + ": F");
}

void backwards()
{
  motors.setSpeeds(-NORMAL_SPEED, -NORMAL_SPEED);
  softSerial.println((String)millis() + ": B");
}

void left()
{
  motors.setSpeeds (-TURN_SPEED, TURN_SPEED);
  softSerial.println((String)millis() + ": L");
}

void right()
{
  motors.setSpeeds(TURN_SPEED, -TURN_SPEED);
  softSerial.println((String)millis() + ": R");
}

void sprintForwards()
{
  motors.setSpeeds(SnormalSpeed, SnormalSpeed);
  softSerial.println((String)millis() + ": Sf");
}

void sprintBackwards()
{
  motors.setSpeeds(-SnormalSpeed, -SnormalSpeed);
  softSerial.println((String)millis() + ": Sb");
}

void sprintLeft()
{
  motors.setSpeeds(-SturnSpeed, SturnSpeed);
  softSerial.println((String)millis() + ": Sl");
}

void sprintRight()
{
  motors.setSpeeds(SturnSpeed, -SturnSpeed);
  softSerial.println((String)millis() + ": Sr");
}

void curveRight()
{
  motors.setSpeeds(SnormalSpeed, NORMAL_SPEED);
  softSerial.println((String)millis() + ": Cr");
}

void curveLeft()
{
  motors.setSpeeds(NORMAL_SPEED, SnormalSpeed);
  softSerial.println((String)millis() + ": Cl");
}

void superSprintRight()
{
  int tempSpeed = SturnSpeed * 2;
  motors.setSpeeds(tempSpeed, -tempSpeed);
}

void superSprintLeft()
{
  int tempSpeed = SturnSpeed * 2;
  motors.setSpeeds(-tempSpeed, tempSpeed);
}

//
// Press functions - Switch between functions with different combinations of pressing
//

void forwardPress()
{
  if (movement == "f")
  {
    sprintForwards();
    movement = "sf";
  }
  else
  {
    forwards();
    movement = "f";
  }
}

void backwardPress()
{
  if (movement == "b")
  {
    sprintBackwards();
    movement = "sb";
  }
  else
  {
    backwards();
    movement = "b";
  }
}

void leftPress()
{
  if (movement == "l")
  {
    sprintLeft();
    movement = "sl";
  }
  else if (movement == "sl")
  {
    superSprintLeft();
    movement = "ssl";
  }
  else
  {
    left();
    movement = "l";
  }
}

void rightPress()
{
  if (movement == "r")
  {
    sprintRight();
    movement = "sr";
  }
  else if (movement == "sr")
  {
    superSprintRight();
    movement = "ssr";
  }
  else
  {
    right();
    movement = "r";
  }
}
