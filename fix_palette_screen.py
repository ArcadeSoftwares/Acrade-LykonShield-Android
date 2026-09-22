import re

with open("app/src/main/java/com/arcadesoftware/lykonshield/WidgetPaletteScreen.kt", "r") as f:
    content = f.read()

# Add scroll import
if "import androidx.compose.foundation.verticalScroll" not in content:
    content = content.replace("import androidx.compose.foundation.layout.*", "import androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.verticalScroll\nimport androidx.compose.foundation.rememberScrollState")

# Add verticalScroll to Column
content = content.replace(
"""            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPadding + 64.dp, bottom = bottomPadding)""",
"""            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = topPadding + 64.dp, bottom = bottomPadding)"""
)

# Replace getPreviewColor
preview_color_func = """fun getPreviewColor(cat: String, palette: String): Color {
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
            "AD" -> "#FF9AA2"
            "TRACKER" -> "#FFB7B2"
            "ANALYTICS" -> "#FFDAC1"
            "SOCIAL" -> "#E2F0CB"
            "TELEMETRY" -> "#B5EAD7"
            "MALWARE" -> "#C7CEEA"
            "OTT" -> "#f8bbd0"
            "DOH" -> "#e1bee7"
            "MINER" -> "#d1c4e9"
            "SPAM" -> "#b2ebf2"
            else -> "#c8e6c9"
        }
        else -> when(cat.uppercase()) {
            "AD" -> "#ff347b"
            "TRACKER" -> "#ffe100"
            "ANALYTICS" -> "#08b9ff"
            "SOCIAL" -> "#d100d1"
            "TELEMETRY" -> "#21ffed"
            "MALWARE" -> "#1bfc4b"
            "OTT" -> "#ff5a96"
            "DOH" -> "#ffea4d"
            "MINER" -> "#3be3ff"
            "SPAM" -> "#e542e5"
            else -> "#5cffd0"
        }
    }
    return Color(android.graphics.Color.parseColor(hex))
}"""

content = re.sub(r'fun getPreviewColor.*?return Color\(android\.graphics\.Color\.parseColor\(hex\)\)\n}', preview_color_func, content, flags=re.DOTALL)

with open("app/src/main/java/com/arcadesoftware/lykonshield/WidgetPaletteScreen.kt", "w") as f:
    f.write(content)
