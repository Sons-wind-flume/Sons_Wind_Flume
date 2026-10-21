#!/bin/bash
set -e
PH=~/Downloads/Phidget22_macosdevel_1/libraries/macos

mvn clean package
rm -rf dist && mkdir -p dist/input
cp target/wind-flume-app-1.0.jar lib/phidget22.jar lib/jfreechart-1.5.6.jar dist/input/
cp "$PH/libphidget22java.jnilib" dist/input/
ditto "$PH/Phidget22.framework" dist/input/Phidget22.framework

# let the wrapper find the framework sitting next to it, then re-sign it
install_name_tool -add_rpath @loader_path dist/input/libphidget22java.jnilib
codesign --force --sign - dist/input/libphidget22java.jnilib

jpackage --type app-image --dest dist --name "SONS Flume" --app-version 1.0 \
  --input dist/input --main-jar wind-flume-app-1.0.jar --main-class org.windflume.Main \
  --java-options "--enable-native-access=ALL-UNNAMED" \
  --java-options "-Djava.library.path=\$APPDIR"