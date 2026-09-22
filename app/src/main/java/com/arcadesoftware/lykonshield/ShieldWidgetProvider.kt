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
        
        if (!isProtectionEnabled) {
            views.setTextColor(R.id.widget_total_count, android.graphics.Color.parseColor("#8E8E93"))
            views.setTextColor(R.id.widget_total_label, android.graphics.Color.parseColor("#8E8E93"))
        }

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
                val color = if (isProtectionEnabled) getColorForCategory(context, cat.key) else android.graphics.Color.parseColor("#8E8E93")

                views.setViewVisibility(rowLayouts[i], android.view.View.VISIBLE)
                views.setImageViewBitmap(rowDots[i], createDotBitmap(color))
                views.setTextViewText(rowNames[i], catName)
                views.setTextViewText(rowPercents[i], "$percent%")
                views.setTextViewText(rowCounts[i], "(${cat.value})")
                
                if (!isProtectionEnabled) {
                    views.setTextColor(rowNames[i], android.graphics.Color.parseColor("#8E8E93"))
                    views.setTextColor(rowPercents[i], android.graphics.Color.parseColor("#8E8E93"))
                    views.setTextColor(rowCounts[i], android.graphics.Color.parseColor("#8E8E93"))
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

        private fun getColorForCategory(context: Context, cat: String): Int {
        val prefs = context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)
        val palette = prefs.getString("widget_palette", "golden")
        
        return when(palette) {
            "golden" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#ffd52e")
                    "TRACKER" -> Color.parseColor("#ffdc5a")
                    "ANALYTICS" -> Color.parseColor("#ffe27e")
                    "SOCIAL" -> Color.parseColor("#ffe99f")
                    "TELEMETRY" -> Color.parseColor("#fff0bf")
                    "MALWARE" -> Color.parseColor("#fff8df")
                    "OTT" -> Color.parseColor("#fbc02d")
                    "DOH" -> Color.parseColor("#f9a825")
                    "MINER" -> Color.parseColor("#f57f17")
                    "SPAM" -> Color.parseColor("#ffb300")
                    else -> Color.parseColor("#ff8f00")
                }
            }
            "fire" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#f5ff25")
                    "TRACKER" -> Color.parseColor("#f6cf23")
                    "ANALYTICS" -> Color.parseColor("#f89f21")
                    "SOCIAL" -> Color.parseColor("#f96f20")
                    "TELEMETRY" -> Color.parseColor("#fb3f1e")
                    "MALWARE" -> Color.parseColor("#fc0e1c")
                    "OTT" -> Color.parseColor("#e65100")
                    "DOH" -> Color.parseColor("#bf360c")
                    "MINER" -> Color.parseColor("#d50000")
                    "SPAM" -> Color.parseColor("#b71c1c")
                    else -> Color.parseColor("#ffab00")
                }
            }
            "rainbow" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#ff0000")
                    "TRACKER" -> Color.parseColor("#ffaa00")
                    "ANALYTICS" -> Color.parseColor("#ffff00")
                    "SOCIAL" -> Color.parseColor("#00ff00")
                    "TELEMETRY" -> Color.parseColor("#014eff")
                    "MALWARE" -> Color.parseColor("#a400ff")
                    "OTT" -> Color.parseColor("#ff5555")
                    "DOH" -> Color.parseColor("#ffcc55")
                    "MINER" -> Color.parseColor("#55ff55")
                    "SPAM" -> Color.parseColor("#5588ff")
                    else -> Color.parseColor("#cc55ff")
                }
            }
            "monochrome" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#e1e4e8")
                    "TRACKER" -> Color.parseColor("#bac0c6")
                    "ANALYTICS" -> Color.parseColor("#939ca3")
                    "SOCIAL" -> Color.parseColor("#646f77")
                    "TELEMETRY" -> Color.parseColor("#33383e")
                    "MALWARE" -> Color.parseColor("#010105")
                    "OTT" -> Color.parseColor("#cfd8dc")
                    "DOH" -> Color.parseColor("#b0bec5")
                    "MINER" -> Color.parseColor("#78909c")
                    "SPAM" -> Color.parseColor("#546e7a")
                    else -> Color.parseColor("#455a64")
                }
            }
            "light_chromatic" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#fffff7")
                    "TRACKER" -> Color.parseColor("#fbfbeb")
                    "ANALYTICS" -> Color.parseColor("#e2e2d4")
                    "SOCIAL" -> Color.parseColor("#c9c9bc")
                    "TELEMETRY" -> Color.parseColor("#b5b5a9")
                    "MALWARE" -> Color.parseColor("#a3a398")
                    "OTT" -> Color.parseColor("#f2f2e4")
                    "DOH" -> Color.parseColor("#ababa0")
                    "MINER" -> Color.parseColor("#bebeae")
                    "SPAM" -> Color.parseColor("#d6d6c8")
                    else -> Color.parseColor("#9a9a90")
                }
            }
            "sky_ocean" -> {
                when(cat.uppercase()) {
                    "AD" -> Color.parseColor("#67c7ff")
                    "TRACKER" -> Color.parseColor("#80d0ff")
                    "ANALYTICS" -> Color.parseColor("#9adaff")
                    "SOCIAL" -> Color.parseColor("#b3e3ff")
                    "TELEMETRY" -> Color.parseColor("#cdecff")
                    "MALWARE" -> Color.parseColor("#e6f6ff")
                    "OTT" -> Color.parseColor("#50baff")
                    "DOH" -> Color.parseColor("#73c8ff")
                    "MINER" -> Color.parseColor("#8bccff")
                    "SPAM" -> Color.parseColor("#a6dbff")
                    else -> Color.parseColor("#c2e9ff")
                }
            }
            else -> {
                when(cat.uppercase()) {
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
                    else -> Color.parseColor("#34C759")
                }
            }
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
            
            // Draw subtle border to prevent dark colors from submerging
            paint.strokeWidth = 30f
            paint.color = Color.parseColor("#30FFFFFF")
            if (sweep > gap) {
                canvas.drawArc(rect, startAngle + gap / 2f, sweep - gap, false, paint)
            } else if (sweep > 0) {
                canvas.drawArc(rect, startAngle + sweep / 2f, 0.1f, false, paint)
            }
            
            // Draw actual segment
            paint.strokeWidth = 26f
            paint.color = getColorForCategory(context, cat.key)
            
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
