import re

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    text = f.read()

# Replace sortedCategories logic
old_logic = '''        val sortedCategories =
            categoryCounts.entries
                .sortedByDescending {
                    it.value
                }'''

new_logic = '''        val adsCount = categoryCounts["AD"] ?: 0
        val trackersCount = categoryCounts["TRACKER"] ?: 0
        
        val sortedCategories = listOf(
            java.util.AbstractMap.SimpleEntry("Ads", adsCount),
            java.util.AbstractMap.SimpleEntry("Trackers", trackersCount),
            java.util.AbstractMap.SimpleEntry("Total", totalBlocks)
        )'''

text = text.replace(old_logic, new_logic)

# In createCategoriesPieChartBitmap, the total should not be `totalBlocks` anymore, 
# because if the rings are Ads, Trackers, Total, the "Total" ring shouldn't use `totalBlocks` as the max if we want it to be a full circle.
# Wait! In createCategoriesPieChartBitmap:
# val maxVal = categories[0].value.toFloat()
# Since categories are (Ads, Trackers, Total), maxVal will be Ads count.
# We want `maxVal` to be `totalBlocks` so the Total ring is a full circle, Ads ring is a fraction of Total, Trackers is a fraction of Total.
# So maxVal should be `totalBlocks` (or `total`). Let's change the maxVal logic.
text = re.sub(
    r'val maxVal = categories\[0\]\.value\.toFloat\(\)',
    r'val maxVal = total.toFloat().coerceAtLeast(1f)',
    text
)

# And because Total is in the list, its sweep will be (Total / Total) * 280f + 20f = 300f. 
# But maybe we want Total to be 100% of the circle, so sweep = 360f?
# For activity rings, they look best if they don't *quite* close completely unless you overlap.
# If sweep is 360, it will look like a full circle. That's fine.
text = re.sub(
    r'val sweep = \(category\.value / maxVal\) \* 280f \+ 20f // Give it some minimum visual weight',
    r'val sweep = if (category.value >= total && total > 0) 360f else (category.value / maxVal) * 340f + 5f',
    text
)

# Also fix categoryName casing
text = text.replace(
'''                val categoryName =
                    category.key
                        .lowercase(Locale.getDefault())
                        .replaceFirstChar {
                            it.uppercase()
                        }''',
'''                val categoryName = category.key'''
)

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(text)

