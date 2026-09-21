import re

# 1. Update Layout (Remove App Icon, Increase Chart Size and text styling)
with open('app/src/main/res/layout/widget_shield.xml', 'r') as f:
    xml_content = f.read()

# Remove app icon
xml_content = re.sub(
    r'<!-- APP ICON -->[\s\S]*?<ImageView[\s\S]*?android:layout_width="40dp"[\s\S]*?/>',
    '',
    xml_content
)

# Enlarge chart slightly for better ring visibility (76dp)
xml_content = xml_content.replace('68dp', '76dp')

# Bump text sizes back up for readability
xml_content = xml_content.replace('18sp', '20sp')
xml_content = xml_content.replace('14sp', '15sp')
# Make the dots slightly bigger
xml_content = xml_content.replace('12dp', '14dp')

with open('app/src/main/res/layout/widget_shield.xml', 'w') as f:
    f.write(xml_content)

# 2. Update Kotlin Provider (Vibrant Colors & Neon Glow)
with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    kt_content = f.read()

# Replace colors
old_colors = 'listOf(Color.parseColor("#FF5F56"), Color.parseColor("#FFBD2E"), Color.parseColor("#27C93F"))'
new_colors = 'listOf(Color.parseColor("#FF0055"), Color.parseColor("#A3FF00"), Color.parseColor("#00E5FF"))'
kt_content = kt_content.replace(old_colors, new_colors)

# Ensure chart matches the 76dp size in XML
kt_content = kt_content.replace('totalBlocks,\n            68', 'totalBlocks,\n            76')

# Add glow effect to rings in chart drawing
old_paint = '''        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }'''
new_paint = '''        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }'''

# I'll just find the drawing loop and add setShadowLayer before drawing the foreground arc
old_draw_loop = '''            // Draw foreground progress
            val sweep = if (category.value >= total && total > 0) 360f else (category.value / maxVal) * 340f + 5f
            paint.color = color
            canvas.drawArc(rect, -90f, sweep.coerceAtMost(360f), false, paint)'''

new_draw_loop = '''            // Draw foreground progress
            val sweep = if (category.value >= total && total > 0) 360f else (category.value / maxVal) * 340f + 5f
            paint.color = color
            // Add a vibrant neon glow
            paint.setShadowLayer(8f, 0f, 0f, Color.argb(180, Color.red(color), Color.green(color), Color.blue(color)))
            canvas.drawArc(rect, -90f, sweep.coerceAtMost(360f), false, paint)
            // Clear shadow for next track
            paint.clearShadowLayer()'''

kt_content = kt_content.replace(old_draw_loop, new_draw_loop)

# Adjust ring track opacity slightly to fit the neon theme better (25% to 15%)
kt_content = kt_content.replace('Color.argb(64,', 'Color.argb(40,')

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(kt_content)

