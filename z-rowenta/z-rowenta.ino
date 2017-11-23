// Pin Definitions
#define LOW_SPEED_PIN 5
#define MEDIUM_SPEED_PIN 6
#define HIGH_SPEED_PIN 7
#define BOOST_SPEED_PIN 8
#define BOOST_BUTTON_PIN 25
#define ONOFF_BUTTON_PIN 2
#define SPEED_BUTTON_PIN 24

// Fan Power Values
#define POWER_OFF 0
#define POWER_ON 255

// Fan Speed Values
#define LOW_SPEED 1
#define MEDIUM_SPEED 2
#define HIGH_SPEED 3
#define BOOST_SPEED 4

// State Change Flags
#define POWER_CHANGED 1
#define SPEED_CHANGED 2

// Z-Wave Channels
#define POWER_CHANNEL 1
#define SPEED_CHANNEL 2

ZUNO_SETUP_CHANNELS(ZUNO_SWITCH_BINARY(getPower, setPower), ZUNO_SWITCH_MULTILEVEL(getSpeed, setSpeed));

// Fan Timings
#define BUTTON_WAIT 25
#define POWER_ON_DELAY 2500

boolean changePower = false;
boolean changeSpeed = false;
unsigned long lastTurnedOnAt = 0;
byte powerValue = 0;
byte speedValue = 0;
byte targetPower = 0;
byte targetSpeed = 0;

void setup() {
  pinMode(LOW_SPEED_PIN, INPUT); // INPUT_PULLUP does not work with Pin 5 -- https://forum.z-wave.me/viewtopic.php?f=3427&t=25688
  pinMode(MEDIUM_SPEED_PIN, INPUT_PULLUP);
  pinMode(HIGH_SPEED_PIN, INPUT_PULLUP);
  pinMode(BOOST_SPEED_PIN, INPUT_PULLUP);

  pinMode(BOOST_BUTTON_PIN, OUTPUT);
  pinMode(ONOFF_BUTTON_PIN, OUTPUT);
  pinMode(SPEED_BUTTON_PIN, OUTPUT);

  pinMode(13, OUTPUT);

  Serial.begin();

  readState();
  zunoSendReport(POWER_CHANNEL);
  zunoSendReport(SPEED_CHANNEL);
}

void loop() {
  byte stateChanged = readState();

  if (powerValue == POWER_ON) {
    int freq = 600 - (speedValue * 100);

    if (millis() % freq < freq / 2) {
      digitalWrite(13, HIGH);
    } else {
      digitalWrite(13, LOW);
    }
  } else {
    digitalWrite(13, LOW);
  }

  if ((stateChanged & POWER_CHANGED) == POWER_CHANGED) {
    Serial.print("powerValue is now ");
    switch(powerValue) {
      case POWER_OFF: Serial.println("0 (OFF)"); break;
      case POWER_ON: Serial.println("255 (ON)"); break;
    }

    if (powerValue == POWER_ON) {
      lastTurnedOnAt = millis();
    }

    zunoSendReport(POWER_CHANNEL);
  }

  if ((stateChanged & SPEED_CHANGED) == SPEED_CHANGED) {
    Serial.print("speedValue is now ");
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
      delay(BUTTON_WAIT);
      digitalWrite(ONOFF_BUTTON_PIN, LOW);
      delay(BUTTON_WAIT);
    }

    changePower = false;
  }

  if (changeSpeed && powerValue == POWER_ON && (lastTurnedOnAt + POWER_ON_DELAY) <= millis()) {
    if (targetSpeed != speedValue) {
      if (targetSpeed == BOOST_SPEED || speedValue == BOOST_SPEED) {
        digitalWrite(BOOST_BUTTON_PIN, HIGH);
        delay(BUTTON_WAIT);
        digitalWrite(BOOST_BUTTON_PIN, LOW);
        delay(BUTTON_WAIT);
        readState();
      }

      while (targetSpeed != speedValue) {
        digitalWrite(SPEED_BUTTON_PIN, HIGH);
        delay(BUTTON_WAIT);
        digitalWrite(SPEED_BUTTON_PIN, LOW);
        delay(BUTTON_WAIT);
        readState();
      }

      zunoSendReport(SPEED_CHANNEL);
    }

    changeSpeed = false;
  }

  delay(50);
}

byte getPower() {
  Serial.print("getPower() # => ");
  Serial.println(powerValue);

  return powerValue;
}

byte getSpeed() {
  Serial.print("getSpeed() # => ");
  Serial.println(speedValue);

  return speedValue;
}

byte readState() {
  byte previousPowerValue = powerValue;
  byte previousSpeedValue = speedValue;

  if (digitalRead(LOW_SPEED_PIN) == HIGH) {
    powerValue = POWER_ON;
    speedValue = LOW_SPEED;
  }
  else if (digitalRead(MEDIUM_SPEED_PIN) == HIGH) {
    powerValue = POWER_ON;
    speedValue = MEDIUM_SPEED;
  }
  else if (digitalRead(HIGH_SPEED_PIN) == HIGH) {
    powerValue = POWER_ON;
    speedValue = HIGH_SPEED;
  }
  else if (digitalRead(BOOST_SPEED_PIN) == HIGH) {
    powerValue = POWER_ON;
    speedValue = BOOST_SPEED;
  }
  else {
    powerValue = POWER_OFF;
  }

  return (previousPowerValue != powerValue ? POWER_CHANGED : 0) | (previousSpeedValue != speedValue ? SPEED_CHANGED : 0);
}

void setPower(byte value) {
  Serial.print("setPower(");
  Serial.print(value);
  Serial.println(")");

  changePower = true;
  targetPower = value == 0 ? POWER_OFF : POWER_ON;
}

void setSpeed(byte value) {
  Serial.print("setSpeed(");
  Serial.print(value);
  Serial.println(")");

  if (powerValue == POWER_OFF) {
    changePower = true;
    targetPower = POWER_ON;
  }

  changeSpeed = true;
  targetSpeed = value;
}
