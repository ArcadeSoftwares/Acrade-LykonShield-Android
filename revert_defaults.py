import re

# 1. Update WidgetPaletteScreen.kt
with open("app/src/main/java/com/arcadesoftware/lykonshield/WidgetPaletteScreen.kt", "r") as f:
    wps_content = f.read()

wps_original_default = """        else -> when(cat.uppercase()) {
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
        }"""

wps_content = re.sub(r'        else -> when\(cat\.uppercase\(\)\) \{\n            "AD" -> "#ff347b".*?else -> "#5cffd0"\n        \}', wps_original_default, wps_content, flags=re.DOTALL)

with open("app/src/main/java/com/arcadesoftware/lykonshield/WidgetPaletteScreen.kt", "w") as f:
    f.write(wps_content)

# 2. Update ShieldWidgetProvider.kt
with open("app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt", "r") as f:
    swp_content = f.read()

swp_original_default = """            else -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#FF3B30")
                    "TRACKER" -> Color.parseColor("#FF9500")
                    "ANALYTICS" -> Color.parseColor("#007AFF")
                    "MALWARE" -> Color.parseColor("#AF52DE")
                    "TELEMETRY" -> Color.parseColor("#30D5C8")
                    "SOCIAL" -> Color.parseColor("#FF2D55")
                    "OTT" -> Color.parseColor("#00C7BE")
                    "DOH" -> Color.parseColor("#5856D6")
                    "MINER" -> Color.parseColor("#8E8E93")
                    "SPAM" -> Color.parseColor("#BF5AF2")
                    else -> Color.parseColor("#34C759")
                }
            }"""

swp_content = re.sub(r'            else -> \{\n                when\(cat\.uppercase\(\)\) \{\n                    "AD" -> Color\.parseColor\("#ff347b"\).*?else -> Color\.parseColor\("#5cffd0"\)\n                \}\n            \}', swp_original_default, swp_content, flags=re.DOTALL)

with open("app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt", "w") as f:
    f.write(swp_content)

