#include <SD.h>
#include <MsTimer2.h>

#define STEP_ACC_SIZE 120
//#define WRITETIME_INTERVER 600000 //10 min (1000mills * 60sec * 10min)
#define WRITETIME_INTERVER 5000

File settingFile, passoFile;
boolean isFileOpened = false;

String inputString = String();         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete

const int MODE_RECORD = 0;
const int MODE_RTSEND = 1;

int mode = MODE_RECORD;

const int LED1 = 9; //red
const int LED2 = 8; //green
boolean bOnLED1 = true;
boolean bOnLED2 = true;


//passometer
int old_x = 0;
int old_y = 0;
int old_z = 0;

int passoCnt = 0;
int passoRecordNum = 0;

//timer interrupt routine (using for save)
void timer() {
  //flash led2 while timer routine running
  bOnLED2 = true;
  digitalWrite(LED2, HIGH);
  
//  Serial.println("***** timer run *****]");
  
  while(isFileOpened); //wait for write end;
  isFileOpened = true;
  int stepCount = passoCnt/2;  
  passoFile = SD.open("passo.pmd", FILE_WRITE);
  passoFile.seek( passoRecordNum*2 );
  passoFile.write( (byte)(stepCount>>8) );
  passoFile.write( (byte)((stepCount<<8)>>8) );
  passoFile.flush();
  passoFile.close();
  
  passoCnt = 0;
  passoRecordNum++;
  wrPassoSetFile();
  
//  Serial.print("passRecordNum : ");
//  Serial.print(passoRecordNum);
//  Serial.println(']');
  
  isFileOpened = false;
}

void wrPassoSetFile() {
  settingFile = SD.open("passo.pms", FILE_WRITE);
  
  settingFile.seek(0);
  settingFile.write( (byte)(passoRecordNum>>8) );
  settingFile.write( (byte)((passoRecordNum<<8)>>8) );
//  settingFile.write(byte(0));settingFile.write(byte(0));
//  if(settingFile.write( (byte)(passoRecordNum>>8) ) ) {Serial.println("write1 done");}
//  if(settingFile.write( (byte)((passoRecordNum<<8)>>8) ) ) {Serial.println("write2 done");}
  settingFile.flush();
  settingFile.close();
//  Serial.println("save setting done");
}

// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(115200);
  Serial.flush();
  
  inputString.reserve(100);
  
  
  //SD Card
//  Serial.print("Initializing SD card...");
  // On the Ethernet Shield, CS is pin 4. It's set as an output by default.
  // Note that even if it's not used as the CS pin, the hardware SS pin 
  // (10 on most Arduino boards, 53 on the Mega) must be left as an output 
  // or the SD library functions will not work. 
  pinMode(10, OUTPUT);
  if (!SD.begin(4)) {  //use digital-pin 11,12,13
//    Serial.println("initialization SD failed!");
    return;
  }
  
  isFileOpened = true;
  if(SD.exists("passo.pms")) {
//    Serial.print("load settings...");
    settingFile = SD.open("passo.pms", FILE_READ);
    passoRecordNum = ( ((int)(settingFile.read()))<<8 ) + (int)(settingFile.read()) ;
    settingFile.close();
  } else {
//    Serial.print("make settings...");
    passoRecordNum = 0;
    wrPassoSetFile();
  }
  isFileOpened = false;
  
//  Serial.println("initialization SD done.");
//  Serial.print("passRecordNum : ");
//  Serial.println(passoRecordNum);
  
  passoCnt = 0;
  
  old_x = analogRead(A0);
  old_y = analogRead(A1);
  old_z = analogRead(A2);
  
  bOnLED1 = true;
  pinMode(LED1, OUTPUT);
  digitalWrite(LED1, HIGH);
  
  bOnLED2 = false;
  pinMode(LED2, OUTPUT);
  digitalWrite(LED2, LOW);
  
  MsTimer2::set(WRITETIME_INTERVER, timer);
  MsTimer2::start();
}

// the loop routine runs over and over again forever:
void loop() {
  if(bOnLED1) {
    bOnLED1 = false;
    digitalWrite(LED1, LOW);
  }
  
  if(bOnLED2) {
    bOnLED2 = false;
    digitalWrite(LED2, LOW);
  }
  
  //request-response
  if (stringComplete) {
    if (inputString == "RQ:PMCONN") {  //PM CONNECT
      Serial.print("RS:CONCTD]");
      
    } else if (inputString == "RQ:DISCON") {  //DISCONNECT
      Serial.print("RS:DISCON]");
      
    } else if (inputString == "RQ:GRTPAS") {  //GET REALTIME PASSOMETER VALUE
      Serial.print("RS:GRTPAS]"); 
      mode = MODE_RTSEND;
      
    } else if (inputString == "RQ:SRTPAS") {  //STOP RT PASSOMETER
      mode = MODE_RECORD;
      
    } else if (inputString == "RQ:GSVPAS") {  //GET SAVED PASSOMETER DATA
      Serial.print("RS:GSVPAS]");  //rm ln
      Serial.print(passoRecordNum);
      Serial.print(']');    //rm ln
      
      while(isFileOpened); //wait for file end;
      isFileOpened = true;
      passoFile = SD.open("passo.pmd", FILE_READ);
      for(int i=0; i<passoRecordNum; i++) {
        int stepCount = ( ((int)(passoFile.read()))<<8 ) + (int)(passoFile.read()) ;
        Serial.print(stepCount);
        Serial.print(']');  //rm ln
//        Serial.write( (byte)(stepCount>>8) );
//        Serial.write( (byte)((stepCount<<8)>>8) );
      }
      passoFile.close();
      Serial.print("RS:ESVPAS]");  //END SAVED PASDATA   //rm ln
      
      //reset record(saved) data file
      passoRecordNum = 0;
      wrPassoSetFile();
      SD.remove("passo.pmd");
      
      isFileOpened = false;
      
    }
    
    //clear the string:
    inputString = "";
    stringComplete = false;
  }
  
  
  // read the input on analog pin 0~2:
  int x = analogRead(A0);
  int y = analogRead(A1);
  int z = analogRead(A2);
  int speed = abs(x + y + z - old_x - old_y - old_z);
  old_x = x;
  old_y = y;
  old_z = z;
  
//  Serial.print("speed:");
//  Serial.print(speed);
//  Serial.print("\tmode:");
//  Serial.println(mode);
  
  
  if (speed > STEP_ACC_SIZE) {
    passoCnt++;
    
    bOnLED1 = true;
    digitalWrite(LED1, HIGH);
  }
  
  switch(mode) {
  case MODE_RTSEND:
    if (speed > STEP_ACC_SIZE) {
      //위로올라갈때 한번, 내려갈때 한번이므로 짝수일때 step +1
      if(passoCnt%2 == 0) {
        Serial.print("speed: ");
        Serial.print(speed);
        Serial.print(" \tStepCount: ");
        Serial.print(passoCnt/2);
        Serial.print(']');  //rm ln
      }
    }
    break;
  
  case MODE_RECORD:
    break;
  }
  
   
  delay(100);
  
}


void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read(); 
    
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == ']') {
      stringComplete = true;
    } else {
      // add it to the inputString:
      inputString += inChar;
    } 
  }
}
