# VortexSync: SONS Wind Flume Strain Gauge Interface

A Java Swing application that reads two strain-gauge channels from a Phidget bridge, converts the voltages to forces, and calculates the water velocity and the drag coefficient (Cd) of a test object during a trial. Results are shown live, on a per-gauge dashboard, and can be exported to a CSV file.

The application also runs **without hardware** (offline mode), which is useful for checking the interface and the export.

## Requirements

- Java 21 or later (not needed for the bundled apps, which include Java)
- Maven 3.9 or later, to build from source
- For real readings: the Phidget22 driver from [phidgets.com](https://www.phidgets.com). Without it, the application starts in offline mode.

## Build and test

```bash
mvn clean package
```

This compiles the code, runs the unit tests and produces `target/wind-flume-app-1.0.jar`.

## Run

From source, with the Phidget library available, see `run.sh` (edit the path to the Phidget22 library to match your computer):

```bash
./run.sh
```

To build a standalone application with Java bundled inside (macOS):

```bash
./package.sh
```

The result is `dist/SONS Flume.app`. If macOS says it cannot verify the developer, right-click the app, choose **Open**, then confirm.

## Using the application

1. Start the application. Both gauge blocks appear, and each shows the settings loaded from `config.json`.
2. Choose **Sphere** on one gauge and **Test Object** on the other. The water velocity is calculated from the sphere's force, so one gauge must be the sphere.
3. Press **START**, then **STOP** when the trial is finished.
4. Click a gauge's chart to open its **dashboard**: current, maximum and minimum velocity, duration, median Cd and maximum force, with a live velocity chart.
5. Press **EXPORT** to save the recording. The popup shows the full path of the file.

In offline mode there is no signal, so velocity and Cd may show `--`. This is expected.

## Configuration

On first run the application creates `~/SONS-Flume/config.json` with default values. Edit this file and restart the application for changes to take effect. If a value is invalid, a warning is shown and the default is used.

| Setting | Meaning |
|---|---|
| `waterDensity` | Water density in kg/m³ (must be above 0) |
| `activeGates` | Channels in use, as a quoted list, for example `"0,1"` |
| `slope0`, `offset0`, `slope1`, `offset1` | Calibration for each gauge: force = slope × voltage + offset |
| `sphereModelId`, `sphereOrientation` | Labels shown on the sphere's gauge block |
| `sphereProjectedArea`, `sphereAttachmentArea`, `sphereCentroidHeight` | Sphere dimensions (areas in m², height in m) |
| `sphereDragCoefficient` | Known drag coefficient of the sphere (must be above 0) |
| `testObjectModelId`, `testObjectOrientation` | Labels for the test object |
| `testObjectProjectedArea`, `testObjectAttachmentArea`, `testObjectCentroidHeight` | Test object dimensions (areas in m², height in m) |
| `testObjectDragCoefficient` | Leave at 0, as it is measured during the trial |

The default calibration values are placeholders. **Calibrate each gauge with known weights and enter the real slope and offset before trusting any measurement.**

To use a different config file, start the application with `-Dwindflume.config=/path/to/config.json`.

## Exported data

Each export is saved as a new file in `~/SONS-Flume/exports/`, named `recording_<date>_<time>.csv`. The file contains the raw gauge readings and spreadsheet formulas for the forces, velocity and Cd, so open it in Excel or a similar program for the calculated values.

## Project structure

| Package | Purpose |
|---|---|
| `org.windflume` | `Main`, the entry point |
| `ui` | Swing interface: main window, gauge blocks, dashboard, settings menu |
| `phidget` | `PhidgetGate`, one Phidget voltage-ratio channel (with offline mode) |
| `calibration` | Voltage to force, water velocity, drag coefficient and trial statistics |
| `recording` | Builds the CSV rows and writes the file |
| `config` | Loads and validates `config.json`; holds the configuration and test objects |
| `importer` | Legacy reader for the old `Config.txt` format. No longer used at startup |

## Tests

The unit tests cover the calibration, the calculations, the trial statistics and the config loader:

```bash
mvn test
```

## Known limitations

- The Phidget22 driver must be installed on the computer for real hardware readings.
- Calibration values must be measured and entered by the user.
- The packaged macOS app is unsigned.