import os

# 1. widget_bg_samsung.xml
os.makedirs('app/src/main/res/drawable', exist_ok=True)
with open('app/src/main/res/drawable/widget_bg_samsung.xml', 'w') as f:
    f.write('''<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <!-- Extremely subtle off-white for true Samsung look, or just white -->
    <solid android:color="#FFFFFF" />
    <corners android:radius="32dp" />
    <!-- A faint shadow/stroke to separate it from white wallpapers -->
    <stroke android:width="1dp" android:color="#F2F2F2" />
</shape>
''')

# 2. widget_shield.xml (Samsung style)
with open('app/src/main/res/layout/widget_shield.xml', 'w') as f:
    f.write('''<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/widget_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal"
    android:gravity="center_vertical"
    android:paddingStart="24dp"
    android:paddingEnd="24dp"
    android:paddingTop="16dp"
    android:paddingBottom="16dp"
    android:background="@drawable/widget_bg_samsung">

    <!-- LEFT: 3 Rows -->
    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:orientation="vertical"
        android:gravity="center_vertical">

        <!-- ROW 0 -->
        <LinearLayout
            android:id="@+id/row_0"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:gravity="bottom"
            android:orientation="horizontal"
            android:layout_marginBottom="12dp">
            <ImageView
                android:id="@+id/row_0_dot"
                android:layout_width="14dp"
                android:layout_height="14dp"
                android:layout_marginEnd="8dp"
                android:layout_marginBottom="4dp"/>
            <TextView
                android:id="@+id/row_0_count"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="#000000"
                android:textSize="22sp"
                android:textStyle="bold"
                android:includeFontPadding="false"/>
            <TextView
                android:id="@+id/row_0_name"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="#444444"
                android:textSize="15sp"
                android:layout_marginStart="4dp"
                android:layout_marginBottom="2dp"
                android:includeFontPadding="false"/>
        </LinearLayout>

        <!-- ROW 1 -->
        <LinearLayout
            android:id="@+id/row_1"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:gravity="bottom"
            android:orientation="horizontal"
            android:layout_marginBottom="12dp">
            <ImageView
                android:id="@+id/row_1_dot"
                android:layout_width="14dp"
                android:layout_height="14dp"
                android:layout_marginEnd="8dp"
                android:layout_marginBottom="4dp"/>
            <TextView
                android:id="@+id/row_1_count"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="#000000"
                android:textSize="22sp"
                android:textStyle="bold"
                android:includeFontPadding="false"/>
            <TextView
                android:id="@+id/row_1_name"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="#444444"
                android:textSize="15sp"
                android:layout_marginStart="4dp"
                android:layout_marginBottom="2dp"
                android:includeFontPadding="false"/>
        </LinearLayout>

        <!-- ROW 2 -->
        <LinearLayout
            android:id="@+id/row_2"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:gravity="bottom"
            android:orientation="horizontal">
            <ImageView
                android:id="@+id/row_2_dot"
                android:layout_width="14dp"
                android:layout_height="14dp"
                android:layout_marginEnd="8dp"
                android:layout_marginBottom="4dp"/>
            <TextView
                android:id="@+id/row_2_count"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="#000000"
                android:textSize="22sp"
                android:textStyle="bold"
                android:includeFontPadding="false"/>
            <TextView
                android:id="@+id/row_2_name"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="#444444"
                android:textSize="15sp"
                android:layout_marginStart="4dp"
                android:layout_marginBottom="2dp"
                android:includeFontPadding="false"/>
        </LinearLayout>

        <TextView
            android:id="@+id/widget_empty_text"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="No blocks yet"
            android:textColor="#8E8E93"
            android:textSize="16sp"
            android:visibility="gone"/>
    </LinearLayout>

    <!-- RIGHT: Chart -->
    <ImageView
        android:id="@+id/widget_pie_chart"
        android:layout_width="96dp"
        android:layout_height="96dp"
        android:layout_marginStart="8dp"
        android:layout_marginEnd="8dp"
        android:scaleType="centerInside"
        android:contentDescription="Blocked categories"/>
</LinearLayout>
''')

# 3. ShieldWidgetProvider.kt
with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    kt_code = f.read()

# Replace the updateAppWidget internals regarding IDs and chart creation
new_kt_code = kt_code.replace(
'''        views.setViewVisibility(
            R.id.widget_legend_container,
            View.VISIBLE
        )''', 
'''        // Legend container removed in new layout'''
).replace(
'''        val rowPercents =
            arrayOf(
                R.id.row_0_percent,
                R.id.row_1_percent,
                R.id.row_2_percent
            )''',
''''''
).replace(
'''        val displayLabel =
            if (todayBlocks > 0) {
                "Today"
            } else {
                "Total"
            }

        views.setTextViewText(
            R.id.widget_total_count,
            formatNumber(displayCount)
        )

        views.setTextViewText(
            R.id.widget_total_label,
            displayLabel
        )''',
''''''
).replace(
'''                views.setTextViewText(
                    rowPercents[i],
                    "$percent%"
                )

                views.setTextViewText(
                    rowCounts[i],
                    "(${formatNumber(category.value)})"
                )''',
'''                views.setTextViewText(
                    rowCounts[i],
                    formatNumber(category.value)
                )

                views.setTextViewText(
                    rowNames[i],
                    "/ $categoryName"
                )'''
)

# Also update the chart drawing function to concentric rings
# Replace `createCategoriesPieChartBitmap` entirely
import re
new_kt_code = re.sub(
    r'private fun createCategoriesPieChartBitmap.*?\n}', 
    '''private fun createCategoriesPieChartBitmap(
        context: Context,
        categories: List<Map.Entry<String, Int>>,
        total: Int,
        sizeDp: Int
    ): Bitmap {
        val density = context.resources.displayMetrics.density
        val sizePx = (sizeDp * density).toInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val strokeWidth = sizePx * 0.12f
        val gap = strokeWidth * 0.4f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        if (total <= 0 || categories.isEmpty()) {
            paint.color = Color.parseColor("#E0E0E0")
            val inset = strokeWidth / 2f
            canvas.drawArc(RectF(inset, inset, sizePx - inset, sizePx - inset), 0f, 360f, false, paint)
            return bitmap
        }

        val maxVal = categories[0].value.toFloat()
        var currentRadius = (sizePx / 2f) - (strokeWidth / 2f)

        for (i in 0 until minOf(3, categories.size)) {
            val category = categories[i]
            val rect = RectF(
                (sizePx / 2f) - currentRadius,
                (sizePx / 2f) - currentRadius,
                (sizePx / 2f) + currentRadius,
                (sizePx / 2f) + currentRadius
            )
            
            val color = getColorForCategory(category.key)
            
            // Draw background track (lightened)
            paint.color = Color.argb(40, Color.red(color), Color.green(color), Color.blue(color))
            canvas.drawArc(rect, 0f, 360f, false, paint)
            
            // Draw foreground progress
            val sweep = (category.value / maxVal) * 280f + 20f // Give it some minimum visual weight
            paint.color = color
            canvas.drawArc(rect, -90f, sweep.coerceAtMost(360f), false, paint)
            
            currentRadius -= (strokeWidth + gap)
        }

        return bitmap
    }''',
    new_kt_code, flags=re.DOTALL
)

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(new_kt_code)
