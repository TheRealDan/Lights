#include <Conceptinetics.h>

DMX_Master DMX(512, 2);

String dmxBuffer = "";
int address, value;
int incoming;
char c;

void setup() {
  Serial.begin(74880);
  Serial.setTimeout(0);

  DMX.enable();
}

void loop() {
  if (Serial.available() > 0) {
    incoming = Serial.read();

    switch (incoming) {
      case 32:
        if (dmxBuffer.length() > 3) {
          value = dmxBuffer.substring(0, 3).toInt();
          for (int i = 3; i < dmxBuffer.length(); i += 3) {
            address = dmxBuffer.substring(i, i + 3).toInt();
            set(address, value);
          }
        }
        dmxBuffer = "";
        break;

      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
        c = incoming;
        dmxBuffer.concat(c);
        break;
    }
  }
}

void set(int address, int value) {
  if (address > 512) return;
  if (address < 1) return;
  if (value > 255) return;
  if (value < 0) return;
  
  DMX.setChannelValue(address, value);
  
  Serial.print(address);
  Serial.print(": ");
  Serial.println(value);
}
