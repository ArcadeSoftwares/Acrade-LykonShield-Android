with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    text = f.read()

# Add back dayKey
replace_target = '''        val appPrefs = context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)'''
with_target = '''        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val dayKey = sdf.format(java.util.Date())
        val appPrefs = context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)'''

# Wait, if there's already an `sdf` and `dayKey` somewhere in updateAppWidget, I should just use it or rename these to `todaySdf` and `todayKeyStr`
# Let's rename to avoid any conflict
text = text.replace(replace_target, with_target.replace('sdf', 'todaySdf').replace('dayKey', 'todayKeyStr'))
text = text.replace('today_total_$dayKey', 'today_total_$todayKeyStr')
text = text.replace('today_ads_$dayKey', 'today_ads_$todayKeyStr')
text = text.replace('today_trackers_$dayKey', 'today_trackers_$todayKeyStr')

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(text)
