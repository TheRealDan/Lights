#include <AltSoftSerial.h>

AltSoftSerial tx;

int incoming;

void setup() {
  Serial.begin(74880);
  Serial.setTimeout(0);
  tx.begin(74880);
  tx.setTimeout(0);
}

void loop() {
    tx.println(Serial.readString());
}
