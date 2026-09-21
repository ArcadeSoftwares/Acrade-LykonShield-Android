#!/bin/bash
sed -i '/import androidx.compose.material3.Icon/a \
import androidx.compose.ui.text.font.FontWeight\
import androidx.compose.material.icons.Icons\
import androidx.compose.material.icons.filled.Home\
import androidx.compose.material.icons.outlined.Home\
import androidx.compose.material.icons.filled.Shield\
import androidx.compose.material.icons.outlined.Shield\
import androidx.compose.material.icons.filled.Block\
import androidx.compose.material.icons.outlined.Block\
import androidx.compose.material.icons.filled.Settings\
import androidx.compose.material.icons.outlined.Settings' app/src/main/java/com/arcadesoftware/lykonshield/MainActivity.kt
