#!/bin/bash
sed -i '/var selectedAppForDetails/a \
    var trafficSearchQuery by remember { mutableStateOf("") }' app/src/main/java/com/arcadesoftware/lykonshield/BlockedScreen.kt
