#!/bin/bash

java --enable-native-access=ALL-UNNAMED \
-Djava.library.path="$HOME/Downloads/Phidget22_macosdevel_1/libraries/macos" \
-cp "target/classes:lib/phidget22.jar:lib/jfreechart-1.5.6.jar" \
org.windflume.Main
