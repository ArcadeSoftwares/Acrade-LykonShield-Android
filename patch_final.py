import re

# 1. Update Widget XML Layout
with open('app/src/main/res/layout/widget_shield.xml', 'r') as f:
    xml_content = f.read()

# Add Logo to the left, shrink chart to 64dp
# First, insert App Icon
app_icon_xml = '''
    <!-- APP ICON -->
    <ImageView
        android:id="@+id/widget_app_icon"
        android:layout_width="28dp"
        android:layout_height="28dp"
        android:layout_marginEnd="16dp"
        android:src="@mipmap/ic_launcher"
        android:scaleType="fitCenter" />
'''

if "APP ICON" not in xml_content:
    xml_content = xml_content.replace('<!-- LEFT: 3 Rows -->', app_icon_xml + '\n    <!-- LEFT: 3 Rows -->')

# Change chart size to 64dp to fix "cirlc is too" big issue
xml_content = xml_content.replace('76dp', '64dp')

with open('app/src/main/res/layout/widget_shield.xml', 'w') as f:
    f.write(xml_content)

# 2. Update Kotlin Provider
with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    kt_content = f.read()

# Replace data fetching logic
old_data_fetch = r'''        val categoryString =[\s\S]*?val sortedCategories = listOf\(
            java\.util\.AbstractMap\.SimpleEntry\("Total", totalBlocks\),
            java\.util\.AbstractMap\.SimpleEntry\("Ads", adsCount\),
            java\.util\.AbstractMap\.SimpleEntry\("Trackers", trackersCount\)
        \)'''

new_data_fetch = '''        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val dayKey = sdf.format(java.util.Date())
        
        val appPrefs = context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)
        val isEnabled = appPrefs.getBoolean("protection_enabled", false)

        val totalBlocks = if (isEnabled) statsPrefs.getInt("today_total_$dayKey", 0) else 0
        val adsCount = if (isEnabled) statsPrefs.getInt("today_ads_$dayKey", 0) else 0
        val trackersCount = if (isEnabled) statsPrefs.getInt("today_trackers_$dayKey", 0) else 0

        val sortedCategories = listOf(
            java.util.AbstractMap.SimpleEntry("Total", totalBlocks),
            java.util.AbstractMap.SimpleEntry("Ads", adsCount),
            java.util.AbstractMap.SimpleEntry("Trackers", trackersCount)
        )'''

kt_content = re.sub(old_data_fetch, new_data_fetch, kt_content)

# Replace the color logic so it greys out if disabled
old_colors = r'val color = listOf\(Color\.parseColor\("#FF0055"\), Color\.parseColor\("#A3FF00"\), Color\.parseColor\("#00E5FF"\)\)\.getOrElse\(i\) \{ getColorForCategory\(category\.key\) \}'
new_colors = r'''val color = if (isEnabled) {
                listOf(Color.parseColor("#FF0055"), Color.parseColor("#A3FF00"), Color.parseColor("#00E5FF")).getOrElse(i) { Color.GRAY }
            } else {
                Color.parseColor("#808080") // Grey out
            }'''
kt_content = re.sub(old_colors, new_colors, kt_content)

# For categoryColor loop:
old_cat_colors = r'val categoryColor = listOf\(Color\.parseColor\("#FF0055"\), Color\.parseColor\("#A3FF00"\), Color\.parseColor\("#00E5FF"\)\)\.getOrElse\(i\) \{ getColorForCategory\(category\.key\) \}'
kt_content = re.sub(old_cat_colors, new_colors.replace('val color =', 'val categoryColor ='), kt_content)

# Update chart size in Kotlin
kt_content = kt_content.replace('totalBlocks,\n            76', 'totalBlocks,\n            64')

# Update context passing to allow isEnabled if needed. We already pass it via reading preferences inside the draw method?
# Wait! createCategoriesPieChartBitmap doesn't have `isEnabled` parameter. 
# It reads `isEnabled` if we inject it or we can just pass it, OR we can just let `total=0` handle the empty state naturally.
# Actually, if totalBlocks == 0, it draws a gray empty circle anyway! 
# Let's see `createCategoriesPieChartBitmap`. If total=0, it does:
# `paint.color = ContextCompat.getColor(context, R.color.widget_track_empty)`
# `canvas.drawArc(..., 0f, 360f)`
# That is already beautifully greyed out! 
# But wait, what if the user disables it, but the numbers still show? We set `totalBlocks = 0` if `!isEnabled` above.
# That means if disabled, total=0, Ads=0, Trackers=0. The chart gets drawn as an empty grey circle automatically!
# And `categoryColor` becomes Grey (from our logic above), making the dots grey.
# And the numbers become "0".
# Let's fix the `getColorForCategory` reference which might error if `isEnabled` is not defined in `createCategoriesPieChartBitmap`.
# Oh! `isEnabled` is NOT available inside `createCategoriesPieChartBitmap` because it's a separate function.
# Let's add `isEnabled: Boolean` to `createCategoriesPieChartBitmap`.

fix_method_sig = r'private fun createCategoriesPieChartBitmap\(\s*context: Context,\s*categories: List<Map\.Entry<String, Int>>,\s*total: Int,\s*sizeDp: Int\s*\): Bitmap \{'
new_method_sig = '''private fun createCategoriesPieChartBitmap(
        context: Context,
        categories: List<Map.Entry<String, Int>>,
        total: Int,
        sizeDp: Int,
        isEnabled: Boolean
    ): Bitmap {'''
kt_content = re.sub(fix_method_sig, new_method_sig, kt_content)

kt_content = kt_content.replace('''createCategoriesPieChartBitmap(
            context,
            sortedCategories,
            totalBlocks,
            64
        )''', '''createCategoriesPieChartBitmap(
            context,
            sortedCategories,
            totalBlocks,
            64,
            isEnabled
        )''')

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(kt_content)

