#!/bin/bash
sed -i 's/val blockedState = rememberLazyListState()/val blockedState = rememberLazyListState()\n                var isAppDetailPopupOpen by remember { mutableStateOf(false) }/g' app/src/main/java/com/arcadesoftware/lykonshield/MainActivity.kt
sed -i 's/val showBars = currentRoute in listOf("home", "blocked", "content_blocking", "settings")/val showBars = currentRoute in listOf("home", "blocked", "content_blocking", "settings") \&\& !isAppDetailPopupOpen/g' app/src/main/java/com/arcadesoftware/lykonshield/MainActivity.kt
