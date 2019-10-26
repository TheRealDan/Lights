# Lights
Lighting control software for desktop - Runs on both Windows and Mac, haven't tested on linux (yet)

## Installation and Setup

### Java JDK

The desktop software for Lights runs in the Java JVM, therefore you will need to install Java.

https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

### Project Setup

The following setup assumes your using IntelliJ, setup may be different for other IDE.

- File > Open > build.gradle 
- Open as Project
- Edit Configurations
- Add New Configuration > Application
- Set Main Class to 'dev.therealdan.lights.desktop.DesktopLauncher'
- Use class path of module 'Lights.desktop.main'
