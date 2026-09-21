#!/bin/bash
sed -i '/var selectedAppForDetails/a \
    LaunchedEffect(selectedAppForDetails) {\n        onPopupStateChange(selectedAppForDetails != null)\n    }' app/src/main/java/com/arcadesoftware/lykonshield/BlockedScreen.kt
