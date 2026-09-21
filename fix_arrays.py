import re
with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    text = f.read()

# Fix the stray bracket
text = text.replace("            )\n\n            )\n\n        val rowCounts =", "            )\n\n        val rowCounts =")

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(text)
