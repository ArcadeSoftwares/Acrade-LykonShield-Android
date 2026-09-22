package com.arcadesoftware.lykonshield

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.catalog.components.LiquidToggle
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop

@Composable
fun WidgetPaletteScreen(
    onBackClick: () -> Unit,
    backdrop: Backdrop,
    topPadding: androidx.compose.ui.unit.Dp,
    bottomPadding: androidx.compose.ui.unit.Dp
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)
    var currentPalette by remember { mutableStateOf(prefs.getString("widget_palette", "golden") ?: "golden") }

    val isLightTheme = LocalIsLightTheme.current
    val contentColor = if (isLightTheme) Color.Black else Color.White
    val screenContentBackdrop = rememberLayerBackdrop()

    val palettes = listOf(
        Pair("default", "Default Vibrant"),
        Pair("golden", "Golden Sun"),
        Pair("fire", "Fire Flame"),
        Pair("rainbow", "Vibrant Rainbow"),
        Pair("monochrome", "Dark Monochrome"),
        Pair("light_chromatic", "White Chromatic"),
        Pair("sky_ocean", "Sky Ocean")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isLightTheme) Color(0xFFF2F2F7) else Color.Black)
    ) {
        Box(
            modifier = Modifier
                .layerBackdrop(screenContentBackdrop)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = topPadding + 64.dp, bottom = bottomPadding)
            ) {
                Text(
                    text = "Widget Palette",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                )

                // Widget Preview
                Text(
                    text = "PREVIEW",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                )
                
                GlassCard(
                    backdrop = backdrop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(220.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Mock Pie Chart
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val stroke = 26f
                                var startAngle = -90f
                                val categories = listOf("AD" to 40, "TRACKER" to 30, "ANALYTICS" to 20, "SOCIAL" to 10)
                                val total = 100
                                val gap = 15f
                                for (cat in categories) {
                                    val sweep = (cat.second.toFloat() / total) * 360f
                                    val color = getPreviewColor(cat.first, currentPalette)
                                    // Draw subtle background border first (slightly larger stroke)
                                    drawArc(
                                        color = if (isLightTheme) Color(0x20000000) else Color(0x30FFFFFF),
                                        startAngle = startAngle + gap / 2f,
                                        sweepAngle = sweep - gap,
                                        useCenter = false,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke + 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                    )
                                    drawArc(
                                        color = color,
                                        startAngle = startAngle + gap / 2f,
                                        sweepAngle = sweep - gap,
                                        useCenter = false,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                    )
                                    startAngle += sweep
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "1,234", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = contentColor)
                                Text(text = "Today", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        
                        // Mock Legend
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            val categories = listOf("Ads" to "AD", "Trackers" to "TRACKER", "Analytics" to "ANALYTICS")
                            categories.forEach { cat ->
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(getPreviewColor(cat.second, currentPalette)))
                                    Text(text = cat.first, color = contentColor, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "PALETTE OPTIONS",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                )

                GlassCard(
                    backdrop = backdrop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        palettes.forEachIndexed { index, palette ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentPalette = palette.first
                                        prefs.edit().putString("widget_palette", palette.first).apply()
                                        // Update app widget via broadcast
                                        val intent = android.content.Intent(context, ShieldWidgetProvider::class.java).apply {
                                            action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                        }
                                        val ids = android.appwidget.AppWidgetManager.getInstance(context).getAppWidgetIds(
                                            android.content.ComponentName(context, ShieldWidgetProvider::class.java)
                                        )
                                        intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                                        context.sendBroadcast(intent)
                                    }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = palette.second, color = contentColor, fontSize = 16.sp)
                                if (currentPalette == palette.first) {
                                    Text(text = "✓", color = Color(android.graphics.Color.parseColor("#007AFF")), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (index < palettes.size - 1) {
                                androidx.compose.material3.HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(start = 16.dp, end = 16.dp))
                            }
                        }
                    }
                }
            }
        }
        
        // Header top bar (transparent) with LiquidButton back button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topPadding + 8.dp, start = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            LiquidButton(
                onClick = { onBackClick() },
                backdrop = screenContentBackdrop,
                modifier = Modifier.size(44.dp),
                shape = { CircleShape },
                surfaceColor = Color.Transparent
            ) {
                Icon(
                    imageVector = ChevronLeftIcon,
                    contentDescription = "Back",
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun getPreviewColor(cat: String, palette: String): Color {
    val hex = when(palette) {
        "golden" -> when(cat.uppercase()) {
            "AD" -> "#ffd52e"
            "TRACKER" -> "#ffdc5a"
            "ANALYTICS" -> "#ffe27e"
            "SOCIAL" -> "#ffe99f"
            "TELEMETRY" -> "#fff0bf"
            "MALWARE" -> "#fff8df"
            "OTT" -> "#fbc02d"
            "DOH" -> "#f9a825"
            "MINER" -> "#f57f17"
            "SPAM" -> "#ffb300"
            else -> "#ff8f00"
        }
        "fire" -> when(cat.uppercase()) {
            "AD" -> "#f5ff25"
            "TRACKER" -> "#f6cf23"
            "ANALYTICS" -> "#f89f21"
            "SOCIAL" -> "#f96f20"
            "TELEMETRY" -> "#fb3f1e"
            "MALWARE" -> "#fc0e1c"
            "OTT" -> "#e65100"
            "DOH" -> "#bf360c"
            "MINER" -> "#d50000"
            "SPAM" -> "#b71c1c"
            else -> "#ffab00"
        }
        "rainbow" -> when(cat.uppercase()) {
            "AD" -> "#ff0000"
            "TRACKER" -> "#ffaa00"
            "ANALYTICS" -> "#ffff00"
            "SOCIAL" -> "#00ff00"
            "TELEMETRY" -> "#014eff"
            "MALWARE" -> "#a400ff"
            "OTT" -> "#ff5555"
            "DOH" -> "#ffcc55"
            "MINER" -> "#55ff55"
            "SPAM" -> "#5588ff"
            else -> "#cc55ff"
        }
        "monochrome" -> when(cat.uppercase()) {
            "AD" -> "#e1e4e8"
            "TRACKER" -> "#bac0c6"
            "ANALYTICS" -> "#939ca3"
            "SOCIAL" -> "#646f77"
            "TELEMETRY" -> "#33383e"
            "MALWARE" -> "#010105"
            "OTT" -> "#cfd8dc"
            "DOH" -> "#b0bec5"
            "MINER" -> "#78909c"
            "SPAM" -> "#546e7a"
            else -> "#455a64"
        }
        "light_chromatic" -> when(cat.uppercase()) {
            "AD" -> "#fffff7"
            "TRACKER" -> "#fbfbeb"
            "ANALYTICS" -> "#e2e2d4"
            "SOCIAL" -> "#c9c9bc"
            "TELEMETRY" -> "#b5b5a9"
            "MALWARE" -> "#a3a398"
            "OTT" -> "#f2f2e4"
            "DOH" -> "#ababa0"
            "MINER" -> "#bebeae"
            "SPAM" -> "#d6d6c8"
            else -> "#9a9a90"
        }
        "sky_ocean" -> when(cat.uppercase()) {
            "AD" -> "#67c7ff"
            "TRACKER" -> "#80d0ff"
            "ANALYTICS" -> "#9adaff"
            "SOCIAL" -> "#b3e3ff"
            "TELEMETRY" -> "#cdecff"
            "MALWARE" -> "#e6f6ff"
            "OTT" -> "#50baff"
            "DOH" -> "#73c8ff"
            "MINER" -> "#8bccff"
            "SPAM" -> "#a6dbff"
            else -> "#c2e9ff"
        }
        else -> when(cat.uppercase()) {
            "AD" -> "#FF3B30"
            "TRACKER" -> "#FF9500"
            "ANALYTICS" -> "#007AFF"
            "MALWARE" -> "#AF52DE"
            "TELEMETRY" -> "#30D5C8"
            "SOCIAL" -> "#FF2D55"
            "OTT" -> "#00C7BE"
            "DOH" -> "#5856D6"
            "MINER" -> "#8E8E93"
            "SPAM" -> "#BF5AF2"
            else -> "#34C759"
        }
    }
    return Color(android.graphics.Color.parseColor(hex))
}
