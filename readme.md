# Lights
Lighting control software for desktop - Runs on both Windows and Mac, haven't tested on linux (yet)

## Installation and Setup

The following setup assumes your using IntelliJ, setup may be different for other IDE.

### Java JDK

The desktop software for Lights runs in the Java JVM, therefore you will need to install the Java JDK.

https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

You may also need to set your gradle JVM to Java 11:

###### Windows

  - File > Settings
  - Build, Execution, Deployment > Build Tools > Gradle
  - Set gradle JVM to Java 11 
 
###### Mac

  - IntelliJ IDEA > Preferences
  - Build, Execution, Deployment > Build Tools > Gradle
  - Set gradle JVM to Java 11 

### Run/Debug Configuration

A run/debug configuration will need to be setup so you can run/debug from source:

- File > Open > build.gradle 
- Open as Project
- Edit Configurations
- Add New Configuration > Application
- Set Main Class to 'dev.therealdan.lights.desktop.DesktopLauncher'
- Use class path of module 'Lights.desktop.main'
