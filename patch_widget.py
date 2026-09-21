import re

with open('app/src/main/res/layout/widget_shield.xml', 'r') as f:
    xml_content = f.read()

# Insert the App Icon at the beginning of the widget_root LinearLayout, before the LEFT 3 rows
app_icon_xml = '''
    <!-- APP ICON -->
    <ImageView
        android:id="@+id/widget_app_icon"
        android:layout_width="38dp"
        android:layout_height="38dp"
        android:layout_marginEnd="16dp"
        android:src="@mipmap/ic_launcher"
        android:background="@drawable/widget_bg_inspired"
        android:elevation="2dp"
        android:scaleType="fitCenter" />
'''
# I'll just put the src directly without background/elevation to keep it clean, as it's transparent/launcher icon.
app_icon_xml = '''
    <!-- APP ICON -->
    <ImageView
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginEnd="20dp"
        android:src="@mipmap/ic_launcher"
        android:scaleType="fitCenter" />
'''

if "APP ICON" not in xml_content:
    xml_content = xml_content.replace('<!-- LEFT: 3 Rows -->', app_icon_xml + '\n    <!-- LEFT: 3 Rows -->')

with open('app/src/main/res/layout/widget_shield.xml', 'w') as f:
    f.write(xml_content)


with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    kt_content = f.read()

# We need to change the colors in updateAppWidget and createCategoriesPieChartBitmap
# Let's define the traffic light colors at the top of createCategoriesPieChartBitmap and in the loop
traffic_colors = "val trafficColors = listOf(Color.parseColor(\\\"#FF5F56\\\"), Color.parseColor(\\\"#FFBD2E\\\"), Color.parseColor(\\\"#27C93F\\\"))"

# For updateAppWidget:
# val categoryColor = getColorForCategory(category.key)
# Change to:
# val categoryColor = listOf(Color.parseColor("#FF5F56"), Color.parseColor("#FFBD2E"), Color.parseColor("#27C93F")).getOrElse(i) { getColorForCategory(category.key) }
kt_content = re.sub(
    r'val categoryColor =\s*getColorForCategory\(\s*category\.key\s*\)',
    r'val categoryColor = listOf(Color.parseColor("#FF5F56"), Color.parseColor("#FFBD2E"), Color.parseColor("#27C93F")).getOrElse(i) { getColorForCategory(category.key) }',
    kt_content
)

# For createCategoriesPieChartBitmap:
# val color = getColorForCategory(category.key)
# Change to:
# val color = listOf(Color.parseColor("#FF5F56"), Color.parseColor("#FFBD2E"), Color.parseColor("#27C93F")).getOrElse(i) { getColorForCategory(category.key) }
kt_content = re.sub(
    r'val color = getColorForCategory\(category\.key\)',
    r'val color = listOf(Color.parseColor("#FF5F56"), Color.parseColor("#FFBD2E"), Color.parseColor("#27C93F")).getOrElse(i) { getColorForCategory(category.key) }',
    kt_content
)

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(kt_content)

