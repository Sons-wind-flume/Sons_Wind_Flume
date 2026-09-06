# Sons Wind Flume - PhidgetBridge Data Acquisition

A lightweight Java tool to connect to a PhidgetBridge 4-Input sensor interface, monitor real-time strain/load cell voltage ratios (V/V), and log timestamped data to CSV.

## Quick Start (Windows)

- **`run_app.bat`**: Starts the main Swing application.
- **`run_diagnostics.bat`**: Runs a quick terminal check to see if your Phidget device and USB drivers are detected.
- **`build.bat`**: Recompiles all Java files.

## Command Line Usage

```bash
# Compile
javac *.java

# Launch App
java App

# Or run connection diagnostics
java Diagnostics
```

## Project Architecture

The codebase is designed with clean separation of concerns and interface-based polymorphism:

- **`BridgeDevice.java`**: Common hardware interface representing a 4-channel bridge device.
- **`PhidgetBridgeDevice.java`**: Real Phidget22 hardware implementation managing USB attach/detach events, bridge excitation, gain (128x), and data listeners.
- **`MockBridgeDevice.java`**: Simulator implementation generating realistic micro-strain noise and drift for testing without hardware.
- **`SensorReading.java`**: Immutable data model representing a single reading (channel, value, timestamp).
- **`BridgeDataListener.java`**: Callback interface for streaming readings to the UI or logger.
- **`CsvDataLogger.java`**: Thread-safe CSV writer with auto-incrementing file naming (`sons_wind_flume_data_0.csv`...).
- **`App.java`**: Swing desktop GUI with live readouts, port controls, and CSV recording.
- **`Diagnostics.java`**: Standalone connection tester.

## Notes & Drivers

If connecting real hardware, make sure the Phidget22 64-bit Windows MSI drivers are installed from [phidgets.com](https://www.phidgets.com/docs/Operating_System_-_Windows).  
If you are developing without hardware, select **"Mock Simulator"** in the app dropdown to test all UI and recording features offline.
