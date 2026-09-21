import re

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldStatsManager.kt', 'r') as f:
    text = f.read()

# Fix the recording logic so todayBlockedTrackers only increments for trackers
record_old = """            totalBlockedTrackers++
            todayBlockedTrackers++
            todayTotalBlocks++
            if (category == BlockCategory.AD) {
                totalAdsBlocked++
                todayAdsBlocked++
            }"""

record_new = """            totalBlockedTrackers++
            todayTotalBlocks++
            if (category == BlockCategory.AD) {
                totalAdsBlocked++
                todayAdsBlocked++
            } else if (category == BlockCategory.TRACKER || category == BlockCategory.ANALYTICS) {
                todayBlockedTrackers++
            } else {
                // If there's another category, just increment trackers as a fallback, 
                // or just leave it. Let's increment it so Ads + Trackers roughly equals Total.
                todayBlockedTrackers++
            }"""
            
text = text.replace(record_old, record_new)

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldStatsManager.kt', 'w') as f:
    f.write(text)
