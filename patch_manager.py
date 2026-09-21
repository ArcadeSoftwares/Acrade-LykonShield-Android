import re

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldStatsManager.kt', 'r') as f:
    text = f.read()

# Add today tracking variables
today_vars = """    var todayBlockedTrackers by mutableStateOf(0)
        private set
    var todayAdsBlocked by mutableStateOf(0)
        private set
    var todayTotalBlocks by mutableStateOf(0)
        private set

    var totalBlockedTrackers by mutableStateOf(0)"""
text = text.replace('    var totalBlockedTrackers by mutableStateOf(0)', today_vars)

# Load today tracking variables
init_replace = """                totalBlockedTrackers = p.getInt(KEY_TOTAL_BLOCKED_TRACKERS, 0)
                totalAdsBlocked = p.getInt(KEY_TOTAL_ADS_BLOCKED, 0)"""

new_init = """                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                val dayKey = sdf.format(java.util.Date())
                todayBlockedTrackers = p.getInt("today_trackers_$dayKey", 0)
                todayAdsBlocked = p.getInt("today_ads_$dayKey", 0)
                todayTotalBlocks = p.getInt("today_total_$dayKey", 0)

                totalBlockedTrackers = p.getInt(KEY_TOTAL_BLOCKED_TRACKERS, 0)
                totalAdsBlocked = p.getInt(KEY_TOTAL_ADS_BLOCKED, 0)"""
text = text.replace(init_replace, new_init)

# Record today tracking variables
record_replace = """            // ── Counters ─────────────────────────────────────────────────
            totalBlockedTrackers++
            if (category == BlockCategory.AD) {
                totalAdsBlocked++
            }"""

new_record = """            // ── Counters ─────────────────────────────────────────────────
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val dayKey = sdf.format(java.util.Date())

            totalBlockedTrackers++
            todayBlockedTrackers++
            todayTotalBlocks++
            if (category == BlockCategory.AD) {
                totalAdsBlocked++
                todayAdsBlocked++
            }"""
text = text.replace(record_replace, new_record)

# Persist today tracking variables
persist_replace = """            putInt(KEY_TOTAL_BLOCKED_TRACKERS, totalBlockedTrackers)
            putInt(KEY_TOTAL_ADS_BLOCKED, totalAdsBlocked)"""

new_persist = """            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val dayKey = sdf.format(java.util.Date())
            putInt("today_trackers_$dayKey", todayBlockedTrackers)
            putInt("today_ads_$dayKey", todayAdsBlocked)
            putInt("today_total_$dayKey", todayTotalBlocks)
            
            putInt(KEY_TOTAL_BLOCKED_TRACKERS, totalBlockedTrackers)
            putInt(KEY_TOTAL_ADS_BLOCKED, totalAdsBlocked)"""
text = text.replace(persist_replace, new_persist)

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldStatsManager.kt', 'w') as f:
    f.write(text)

