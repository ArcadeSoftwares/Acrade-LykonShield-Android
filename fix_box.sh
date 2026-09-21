#!/bin/bash
sed -i 's/                .layerBackdrop(dialogBackdrop)\n        )\n        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {/                .layerBackdrop(dialogBackdrop),\n            contentAlignment = Alignment.TopCenter\n        ) {/g' app/src/main/java/com/arcadesoftware/lykonshield/BlockedScreen.kt
