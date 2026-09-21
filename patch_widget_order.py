with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    text = f.read()

new_logic = '''        val sortedCategories = listOf(
            java.util.AbstractMap.SimpleEntry("Total", totalBlocks),
            java.util.AbstractMap.SimpleEntry("Ads", adsCount),
            java.util.AbstractMap.SimpleEntry("Trackers", trackersCount)
        )'''

text = text.replace('''        val sortedCategories = listOf(
            java.util.AbstractMap.SimpleEntry("Ads", adsCount),
            java.util.AbstractMap.SimpleEntry("Trackers", trackersCount),
            java.util.AbstractMap.SimpleEntry("Total", totalBlocks)
        )''', new_logic)

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(text)
