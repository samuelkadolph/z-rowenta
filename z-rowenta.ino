#define LOW_SPEED_PIN 5
#define MEDIUM_SPEED_PIN 6
#define HIGH_SPEED_PIN 7
#define BOOST_SPEED_PIN 8
#define BOOST_BUTTON_PIN 9
#define ONOFF_BUTTON_PIN 10
#define SPEED_BUTTON_PIN 11

#define ON 1
#define OFF 0

#define LOW_SPEED 1
#define MEDIUM_SPEED 2
#define HIGH_SPEED 3
#define BOOST_SPEED 4

#define POWER_CHANGED 1
#define SPEED_CHANGED 2
#define POWER_CHANNEL 1
#define SPEED_CHANNEL 2

ZUNO_SETUP_SLEEPING_MODE(ZUNO_SLEEPING_MODE_ALWAYS_AWAKE);
ZUNO_SETUP_CHANNELS(ZUNO_SWITCH_BINARY(getPower, setPower), ZUNO_SWITCH_MULTILEVEL(getSpeed, setSpeed));

byte powerValue = 0;
byte speedValue = 0;
byte targetPower = 0;
byte targetSpeed = 0;
boolean changePower = false;
boolean changeSpeed = false;

void setup() {
  pinMode(LOW_SPEED_PIN, INPUT);
  pinMode(MEDIUM_SPEED_PIN, INPUT);
  pinMode(HIGH_SPEED_PIN, INPUT);
  pinMode(BOOST_SPEED_PIN, INPUT);

  pinMode(BOOST_BUTTON_PIN, OUTPUT);
  pinMode(ONOFF_BUTTON_PIN, OUTPUT);
  pinMode(SPEED_BUTTON_PIN, OUTPUT);

  digitalWrite(ONOFF_BUTTON_PIN, LOW);

  pinMode(13, OUTPUT);

  Serial.begin();

//  readState();
//  sendReport();
}

void loop() {
  byte stateChanged = readState();

  if (powerValue == ON) {
    int freq = 500 - (speedValue * 100);

    if (millis() % freq < freq / 2) {
      digitalWrite(13, HIGH);
    } else {
      digitalWrite(13, LOW);
    }
  } else {
    digitalWrite(13, LOW);
  }

  if ((stateChanged & POWER_CHANGED) == POWER_CHANGED) {
    Serial.print("powerValue = ");
    switch(powerValue) {
      case ON: Serial.println("1 (ON)"); break;
      case OFF: Serial.println("0 (OFF)"); break;
    }

    zunoSendReport(POWER_CHANNEL);
  }

  if ((stateChanged & SPEED_CHANGED) == SPEED_CHANGED) {
    Serial.print("speedValue = ");
    switch(speedValue) {
      case LOW_SPEED: Serial.println("1 (LOW)"); break;
      case MEDIUM_SPEED: Serial.println("2 (MEDIUM)"); break;
      case HIGH_SPEED: Serial.println("3 (HIGH)"); break;
      case BOOST_SPEED: Serial.println("4 (BOOST)"); break;
    }

    zunoSendReport(SPEED_CHANNEL);
  }

  if (changePower) {
    if (targetPower != powerValue) {
      digitalWrite(ONOFF_BUTTON_PIN, HIGH);
    } else {
      digitalWrite(ONOFF_BUTTON_PIN, LOW);
      changePower = false;
    }
  }

  delay(50);
}

byte getPower() {
  Serial.print("getPower() = ");
  Serial.println(powerValue);
  return powerValue;
}

byte getSpeed() {
  Serial.print("getSpeed() = ");
  Serial.println(speedValue);
  return speedValue;
}

byte readState() {
  byte previousPowerValue = powerValue;
  byte previousSpeedValue = speedValue;

  if (digitalRead(LOW_SPEED_PIN) == HIGH) {
    powerValue = ON;
    speedValue = LOW_SPEED;
  }
  else if (digitalRead(MEDIUM_SPEED_PIN) == HIGH) {
    powerValue = ON;
    speedValue = MEDIUM_SPEED;
  }
  else if (digitalRead(HIGH_SPEED_PIN) == HIGH) {
    powerValue = ON;
    speedValue = HIGH_SPEED;
  }
  else if (digitalRead(BOOST_SPEED_PIN) == HIGH) {
    powerValue = ON;
    speedValue = BOOST_SPEED;
  }
  else {
    powerValue = OFF;
  }

  return (previousPowerValue != powerValue ? POWER_CHANGED : 0) | (previousSpeedValue != speedValue ? SPEED_CHANGED : 0);
}

void sendReport() {
  zunoSendReport(1);
  zunoSendReport(2);
}

void setPower(byte value) {
  Serial.print("setPower(");
  Serial.print(value);
  Serial.println(")");
  changePower = true;
  targetPower = value == 0 ? OFF : ON;
}

void setSpeed(byte value) {
  Serial.print("setSpeed(");
  Serial.print(value);
  Serial.println(")");
  changeSpeed = true;
  targetSpeed = value;
}

