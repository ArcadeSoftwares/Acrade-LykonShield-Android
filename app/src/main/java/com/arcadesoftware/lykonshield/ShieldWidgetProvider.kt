package com.arcadesoftware.lykonshield

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color

class ShieldWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = android.content.ComponentName(context, ShieldWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val statsPrefs = context.getSharedPreferences("lykon_shield_stats", Context.MODE_PRIVATE)

        val defaultPrefs = context.getSharedPreferences("lykon_shield_prefs", android.content.Context.MODE_PRIVATE)
        val isProtectionEnabled = defaultPrefs.getBoolean("protection_enabled", false)

        val views = RemoteViews(context.packageName, R.layout.widget_shield)

        // Extract today's blocks
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val todayKey = sdf.format(java.util.Date())
        val dailyString = statsPrefs.getString("daily_blocks", "") ?: ""
        var todayBlocks = 0
        if (dailyString.isNotEmpty()) {
            dailyString.split(",").forEach { entry ->
                val parts = entry.split(":")
                if (parts.size == 2 && parts[0] == todayKey) {
                    todayBlocks = parts[1].toIntOrNull() ?: 0
                }
            }
        }

        val categoryString = statsPrefs.getString("today_category_blocks_$todayKey", "") ?: ""
        val categoryCounts = mutableMapOf<String, Int>()
        var totalBlocks = 0
        
        if (categoryString.isNotEmpty()) {
            categoryString.split(",").forEach { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    parts[1].toIntOrNull()?.let { count ->
                        categoryCounts[parts[0]] = count
                        totalBlocks += count
                    }
                }
            }
        }

        val sortedCategories = categoryCounts.entries.sortedByDescending { it.value }

        // Set Today's Daily Count Text (or fall back to total if daily not yet bucketed)
        val displayCount = if (todayBlocks > 0) todayBlocks else totalBlocks
        val displayLabel = if (todayBlocks > 0) "Today" else "Total"
        views.setTextViewText(R.id.widget_total_count, displayCount.toString())
        views.setTextViewText(R.id.widget_total_label, displayLabel)

        // Draw Pie Chart
        // The default 5 categories to ALWAYS show
        val defaultCats = listOf("AD", "TRACKER", "ANALYTICS", "MALWARE", "SOCIAL")
        
        // Merge the actual blocked categories with the defaults
        val mergedMap = mutableMapOf<String, Int>()
        defaultCats.forEach { mergedMap[it] = 0 }
        
        sortedCategories.forEach {
            val key = it.key.uppercase()
            mergedMap[key] = it.value
        }
        
        val mergedSorted = mergedMap.entries.sortedByDescending { it.value }
        
        val displayCategories = mutableListOf<Map.Entry<String, Int>>()
        if (mergedSorted.size <= 6) {
            displayCategories.addAll(mergedSorted)
        } else {
            displayCategories.addAll(mergedSorted.take(5))
            val extraCount = mergedSorted.drop(5).sumOf { it.value }
            if (extraCount > 0) {
                displayCategories.add(java.util.AbstractMap.SimpleEntry("Extra", extraCount))
            }
        }
        
        val bitmap = createCategoriesPieChartBitmap(context, displayCategories, totalBlocks, isProtectionEnabled)
        views.setImageViewBitmap(R.id.widget_pie_chart, bitmap)

        // Setup Legend Rows
        val rowLayouts = listOf(R.id.row_0, R.id.row_1, R.id.row_2, R.id.row_3, R.id.row_4, R.id.row_5)
        val rowDots = listOf(R.id.row_0_dot, R.id.row_1_dot, R.id.row_2_dot, R.id.row_3_dot, R.id.row_4_dot, R.id.row_5_dot)
        val rowNames = listOf(R.id.row_0_name, R.id.row_1_name, R.id.row_2_name, R.id.row_3_name, R.id.row_4_name, R.id.row_5_name)
        val rowPercents = listOf(R.id.row_0_percent, R.id.row_1_percent, R.id.row_2_percent, R.id.row_3_percent, R.id.row_4_percent, R.id.row_5_percent)
        val rowCounts = listOf(R.id.row_0_count, R.id.row_1_count, R.id.row_2_count, R.id.row_3_count, R.id.row_4_count, R.id.row_5_count)


        for (i in 0 until 6) {
            if (i < displayCategories.size) {
                val cat = displayCategories[i]
                val catName = cat.key.lowercase().replaceFirstChar { it.uppercase() }
                val percent = if (totalBlocks > 0) ((cat.value.toFloat() / totalBlocks) * 100).toInt() else 0
                val color = if (isProtectionEnabled) getColorForCategory(cat.key) else android.graphics.Color.parseColor("#8E8E93")

                views.setViewVisibility(rowLayouts[i], android.view.View.VISIBLE)
                views.setImageViewBitmap(rowDots[i], createDotBitmap(color))
                views.setTextViewText(rowNames[i], catName)
                views.setTextViewText(rowPercents[i], "$percent%")
                views.setTextViewText(rowCounts[i], "(${cat.value})")
                
                if (!isProtectionEnabled) {
                    views.setTextColor(rowNames[i], android.graphics.Color.parseColor("#8E8E93"))
                    views.setTextColor(rowPercents[i], android.graphics.Color.parseColor("#8E8E93"))
                    views.setTextColor(rowCounts[i], android.graphics.Color.parseColor("#8E8E93"))
                } else {
                    views.setTextColor(rowNames[i], androidx.core.content.ContextCompat.getColor(context, R.color.widget_text_primary))
                    views.setTextColor(rowPercents[i], androidx.core.content.ContextCompat.getColor(context, R.color.widget_text_primary))
                    views.setTextColor(rowCounts[i], androidx.core.content.ContextCompat.getColor(context, R.color.widget_text_secondary))
                }
            } else {
                views.setViewVisibility(rowLayouts[i], android.view.View.GONE)
            }
        }
        // Open app when clicking background
        val appIntent = Intent(context, MainActivity::class.java)
        val appPendingIntent = PendingIntent.getActivity(
            context,
            1,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, appPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getColorForCategory(cat: String): Int {
        return when(cat.uppercase()) {
            "AD" -> Color.parseColor("#FF3B30")
            "TRACKER" -> Color.parseColor("#FF9500")
            "ANALYTICS" -> Color.parseColor("#007AFF")
            "MALWARE" -> Color.parseColor("#AF52DE")
            "TELEMETRY" -> Color.parseColor("#30D5C8")
            "SOCIAL" -> Color.parseColor("#FF2D55")
            "OTT" -> Color.parseColor("#00C7BE")
            "DOH" -> Color.parseColor("#5856D6")
            "MINER" -> Color.parseColor("#8E8E93")
            "SPAM" -> Color.parseColor("#BF5AF2")
            "OTHER" -> Color.parseColor("#34C759")
            else -> Color.parseColor("#34C759")
        }
    }

    private fun createDotBitmap(color: Int): Bitmap {
        val size = 24
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.style = Paint.Style.FILL
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        return bitmap
    }

    private fun createCategoriesPieChartBitmap(context: Context, categories: List<Map.Entry<String, Int>>, total: Int, isProtectionEnabled: Boolean): Bitmap {
        val size = 260
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 26f
            strokeCap = Paint.Cap.ROUND
        }
        
        val rect = RectF(20f, 20f, size - 20f, size - 20f)
        
        if (total == 0 || categories.isEmpty() || !isProtectionEnabled) {
            paint.color = androidx.core.content.ContextCompat.getColor(context, R.color.widget_track_empty)
            canvas.drawArc(rect, 0f, 360f, false, paint)
            return bitmap
        }
        
        var startAngle = -90f
        val gap = if (categories.size > 1) 20f else 0f

        for (cat in categories) {
            val sweep = (cat.value.toFloat() / total) * 360f
            paint.color = getColorForCategory(cat.key)
            
            if (sweep > gap) {
                canvas.drawArc(rect, startAngle + gap / 2f, sweep - gap, false, paint)
            } else if (sweep > 0) {
                // For segments too small to fit the gap, draw a circular dot (0.1 sweep)
                // in the center of their allocated space to minimize overlap
                canvas.drawArc(rect, startAngle + sweep / 2f, 0.1f, false, paint)
            }
            startAngle += sweep
        }
        
        return bitmap
    }
}
