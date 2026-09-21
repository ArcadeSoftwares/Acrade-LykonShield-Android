with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    text = f.read()

# Replace duplicate sdf and dayKey declarations
text = text.replace('''        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val dayKey = sdf.format(java.util.Date())
        
        val appPrefs = context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)''',
'''        val appPrefs = context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)''')

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(text)
