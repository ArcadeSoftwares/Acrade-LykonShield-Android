#!/bin/bash
sed -i '/import androidx.compose.material3.Icon/a \
import androidx.compose.material.icons.Icons\
import androidx.compose.material.icons.filled.PieChart\
import androidx.compose.material.icons.outlined.PieChart\
import androidx.compose.material.icons.filled.DateRange\
import androidx.compose.material.icons.outlined.DateRange\
import androidx.compose.material.icons.filled.ShowChart\
import androidx.compose.material.icons.outlined.ShowChart\
import androidx.compose.material.icons.filled.Apps\
import androidx.compose.material.icons.outlined.Apps\
import androidx.compose.material.icons.filled.Public\
import androidx.compose.material.icons.outlined.Public\
import androidx.compose.material.icons.filled.SwapVert\
import androidx.compose.material.icons.outlined.SwapVert' app/src/main/java/com/arcadesoftware/lykonshield/BlockedScreen.kt
