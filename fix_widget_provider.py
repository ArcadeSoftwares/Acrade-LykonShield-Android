import re

with open("app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt", "r") as f:
    content = f.read()

new_mapping = """    private fun getColorForCategory(context: Context, cat: String): Int {
        val prefs = context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)
        val palette = prefs.getString("widget_palette", "golden")
        
        return when(palette) {
            "golden" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#ffd52e")
                    "TRACKER" -> Color.parseColor("#ffdc5a")
                    "ANALYTICS" -> Color.parseColor("#ffe27e")
                    "SOCIAL" -> Color.parseColor("#ffe99f")
                    "TELEMETRY" -> Color.parseColor("#fff0bf")
                    "MALWARE" -> Color.parseColor("#fff8df")
                    "OTT" -> Color.parseColor("#fbc02d")
                    "DOH" -> Color.parseColor("#f9a825")
                    "MINER" -> Color.parseColor("#f57f17")
                    "SPAM" -> Color.parseColor("#ffb300")
                    else -> Color.parseColor("#ff8f00")
                }
            }
            "fire" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#f5ff25")
                    "TRACKER" -> Color.parseColor("#f6cf23")
                    "ANALYTICS" -> Color.parseColor("#f89f21")
                    "SOCIAL" -> Color.parseColor("#f96f20")
                    "TELEMETRY" -> Color.parseColor("#fb3f1e")
                    "MALWARE" -> Color.parseColor("#fc0e1c")
                    "OTT" -> Color.parseColor("#e65100")
                    "DOH" -> Color.parseColor("#bf360c")
                    "MINER" -> Color.parseColor("#d50000")
                    "SPAM" -> Color.parseColor("#b71c1c")
                    else -> Color.parseColor("#ffab00")
                }
            }
            "rainbow" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#ff0000")
                    "TRACKER" -> Color.parseColor("#ffaa00")
                    "ANALYTICS" -> Color.parseColor("#ffff00")
                    "SOCIAL" -> Color.parseColor("#00ff00")
                    "TELEMETRY" -> Color.parseColor("#014eff")
                    "MALWARE" -> Color.parseColor("#a400ff")
                    "OTT" -> Color.parseColor("#ff5555")
                    "DOH" -> Color.parseColor("#ffcc55")
                    "MINER" -> Color.parseColor("#55ff55")
                    "SPAM" -> Color.parseColor("#5588ff")
                    else -> Color.parseColor("#cc55ff")
                }
            }
            "monochrome" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#e1e4e8")
                    "TRACKER" -> Color.parseColor("#bac0c6")
                    "ANALYTICS" -> Color.parseColor("#939ca3")
                    "SOCIAL" -> Color.parseColor("#646f77")
                    "TELEMETRY" -> Color.parseColor("#33383e")
                    "MALWARE" -> Color.parseColor("#010105")
                    "OTT" -> Color.parseColor("#cfd8dc")
                    "DOH" -> Color.parseColor("#b0bec5")
                    "MINER" -> Color.parseColor("#78909c")
                    "SPAM" -> Color.parseColor("#546e7a")
                    else -> Color.parseColor("#455a64")
                }
            }
            "light_chromatic" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#FF9AA2")
                    "TRACKER" -> Color.parseColor("#FFB7B2")
                    "ANALYTICS" -> Color.parseColor("#FFDAC1")
                    "SOCIAL" -> Color.parseColor("#E2F0CB")
                    "TELEMETRY" -> Color.parseColor("#B5EAD7")
                    "MALWARE" -> Color.parseColor("#C7CEEA")
                    "OTT" -> Color.parseColor("#f8bbd0")
                    "DOH" -> Color.parseColor("#e1bee7")
                    "MINER" -> Color.parseColor("#d1c4e9")
                    "SPAM" -> Color.parseColor("#b2ebf2")
                    else -> Color.parseColor("#c8e6c9")
                }
            }
            else -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#ff347b")
                    "TRACKER" -> Color.parseColor("#ffe100")
                    "ANALYTICS" -> Color.parseColor("#08b9ff")
                    "SOCIAL" -> Color.parseColor("#d100d1")
                    "TELEMETRY" -> Color.parseColor("#21ffed")
                    "MALWARE" -> Color.parseColor("#1bfc4b")
                    "OTT" -> Color.parseColor("#ff5a96")
                    "DOH" -> Color.parseColor("#ffea4d")
                    "MINER" -> Color.parseColor("#3be3ff")
                    "SPAM" -> Color.parseColor("#e542e5")
                    else -> Color.parseColor("#5cffd0")
                }
            }
        }
    }"""

content = re.sub(r'private fun getColorForCategory\(context: Context, cat: String\): Int \{.*?\n    \}', new_mapping, content, flags=re.DOTALL)

with open("app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt", "w") as f:
    f.write(content)
