#include <lib_dmx.h>
#include <AltSoftSerial.h>

AltSoftSerial rx;

String dmxBuffer = "";
int address, value;
int incoming;
char c;

int led = 13;

void setup() {
  ArduinoDmx0.set_tx_address(1);
  ArduinoDmx0.set_tx_channels(512);
  ArduinoDmx0.init_tx(0);

  rx.begin(74880);
  rx.setTimeout(0);
}

void loop() {
  if (rx.available() > 0) {
    incoming = rx.read();

    switch(incoming) {
      case 32:
        if (dmxBuffer.length() > 3) {
          value = dmxBuffer.substring(0, 3).toInt();
          for (int i = 3; i < dmxBuffer.length(); i += 3) {
            address = dmxBuffer.substring(i, i + 3).toInt();
            dmx(address, value);
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

void loopV1() {
  if (rx.available() > 0) {
    incoming = rx.read();

    switch(incoming) {
      case 32:
        if (dmxBuffer.length() > 3) {
          address = dmxBuffer.substring(0, 3).toInt();
          value = dmxBuffer.substring(3, min(dmxBuffer.length(), 6)).toInt();
          dmx(address, value);
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

void dmx(int address, int value) {
  if (address > 512) address = 512;
  if (address < 1) address = 1;
  if (value > 255) value = 255;
  if (value < 0) value = 0;

  ArduinoDmx0.TxBuffer[address - 1] = value;
}

int getdmx(int address) {
  return ArduinoDmx0.TxBuffer[address - 1];
}

