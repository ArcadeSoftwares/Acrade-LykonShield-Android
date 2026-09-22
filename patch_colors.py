import re

# 1. WidgetPaletteScreen.kt
with open("app/src/main/java/com/arcadesoftware/lykonshield/WidgetPaletteScreen.kt", "r") as f:
    wps_content = f.read()

# Update list of palettes
wps_content = wps_content.replace('Pair("light_chromatic", "Light Chromatic")', 'Pair("light_chromatic", "White Chromatic"),\n        Pair("sky_ocean", "Sky Ocean")')

# Replace light_chromatic in getPreviewColor and add sky_ocean
new_preview_colors = """        "light_chromatic" -> when(cat.uppercase()) {
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
        }"""
wps_content = re.sub(r'        "light_chromatic" -> when\(cat\.uppercase\(\)\) \{\n.*?else -> "#c8e6c9"\n        \}', new_preview_colors, wps_content, flags=re.DOTALL)

with open("app/src/main/java/com/arcadesoftware/lykonshield/WidgetPaletteScreen.kt", "w") as f:
    f.write(wps_content)

# 2. ShieldWidgetProvider.kt
with open("app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt", "r") as f:
    swp_content = f.read()

new_widget_colors = """            "light_chromatic" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#fffff7")
                    "TRACKER" -> Color.parseColor("#fbfbeb")
                    "ANALYTICS" -> Color.parseColor("#e2e2d4")
                    "SOCIAL" -> Color.parseColor("#c9c9bc")
                    "TELEMETRY" -> Color.parseColor("#b5b5a9")
                    "MALWARE" -> Color.parseColor("#a3a398")
                    "OTT" -> Color.parseColor("#f2f2e4")
                    "DOH" -> Color.parseColor("#ababa0")
                    "MINER" -> Color.parseColor("#bebeae")
                    "SPAM" -> Color.parseColor("#d6d6c8")
                    else -> Color.parseColor("#9a9a90")
                }
            }
            "sky_ocean" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#67c7ff")
                    "TRACKER" -> Color.parseColor("#80d0ff")
                    "ANALYTICS" -> Color.parseColor("#9adaff")
                    "SOCIAL" -> Color.parseColor("#b3e3ff")
                    "TELEMETRY" -> Color.parseColor("#cdecff")
                    "MALWARE" -> Color.parseColor("#e6f6ff")
                    "OTT" -> Color.parseColor("#50baff")
                    "DOH" -> Color.parseColor("#73c8ff")
                    "MINER" -> Color.parseColor("#8bccff")
                    "SPAM" -> Color.parseColor("#a6dbff")
                    else -> Color.parseColor("#c2e9ff")
                }
            }"""
swp_content = re.sub(r'            "light_chromatic" -> \{\n                when\(cat\.uppercase\(\)\) \{\n.*?else -> Color\.parseColor\("#c8e6c9"\)\n                \}\n            \}', new_widget_colors, swp_content, flags=re.DOTALL)

with open("app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt", "w") as f:
    f.write(swp_content)

