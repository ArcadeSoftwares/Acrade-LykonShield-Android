import re
with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldStatsManager.kt', 'r') as f:
    text = f.read()

# I will just rename the ones I added to `todaySdf` and `todayDayKey`
text = text.replace('''            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val dayKey = sdf.format(java.util.Date())

            totalBlockedTrackers++''', '''            val todaySdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val todayDayKey = todaySdf.format(java.util.Date())

            totalBlockedTrackers++''')

text = text.replace('''            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val dayKey = sdf.format(java.util.Date())
            putInt("today_trackers_$dayKey", todayBlockedTrackers)''',
'''            val todaySdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val todayDayKey = todaySdf.format(java.util.Date())
            putInt("today_trackers_$todayDayKey", todayBlockedTrackers)
            putInt("today_ads_$todayDayKey", todayAdsBlocked)
            putInt("today_total_$todayDayKey", todayTotalBlocks)''')

text = text.replace('''            putInt("today_ads_$dayKey", todayAdsBlocked)
            putInt("today_total_$dayKey", todayTotalBlocks)''', '')

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldStatsManager.kt', 'w') as f:
    f.write(text)
