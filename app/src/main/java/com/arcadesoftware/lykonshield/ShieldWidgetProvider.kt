package com.arcadesoftware.lykonshield

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import android.graphics.RectF
import android.view.View
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShieldWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {

            val appWidgetManager =
                AppWidgetManager.getInstance(context)

            val componentName =
                android.content.ComponentName(
                    context,
                    ShieldWidgetProvider::class.java
                )

            val appWidgetIds =
                appWidgetManager.getAppWidgetIds(componentName)

            onUpdate(
                context,
                appWidgetManager,
                appWidgetIds
            )
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle
    ) {
        super.onAppWidgetOptionsChanged(
            context,
            appWidgetManager,
            appWidgetId,
            newOptions
        )

        updateAppWidget(
            context,
            appWidgetManager,
            appWidgetId
        )
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val statsPrefs =
            context.getSharedPreferences(
                "lykon_shield_stats",
                Context.MODE_PRIVATE
            )

        val views =
            RemoteViews(
                context.packageName,
                R.layout.widget_shield
            )

        // =========================================================
        // GET TODAY'S BLOCK COUNT
        // =========================================================

        val sdf = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val todayKey =
            sdf.format(Date())

        val dailyString =
            statsPrefs.getString(
                "daily_blocks",
                ""
            ) ?: ""

        var todayBlocks = 0

        if (dailyString.isNotEmpty()) {

            dailyString
                .split(",")
                .forEach { entry ->

                    val parts =
                        entry.split(":")

                    if (
                        parts.size == 2 &&
                        parts[0] == todayKey
                    ) {

                        todayBlocks =
                            parts[1].toIntOrNull()
                                ?: 0
                    }
                }
        }

        // =========================================================
        // GET CATEGORY DATA
        // =========================================================

        val categoryString =
            statsPrefs.getString(
                "category_blocks",
                ""
            ) ?: ""

        val categoryCounts =
            mutableMapOf<String, Int>()

        var totalBlocks = 0

        if (categoryString.isNotEmpty()) {

            categoryString
                .split(",")
                .forEach { entry ->

                    val parts =
                        entry.split(":")

                    if (parts.size == 2) {

                        val count =
                            parts[1].toIntOrNull()

                        if (count != null) {

                            categoryCounts[
                                parts[0]
                            ] = count

                            totalBlocks += count
                        }
                    }
                }
        }

        val adsCount = categoryCounts["AD"] ?: 0
        val trackersCount = categoryCounts["TRACKER"] ?: 0
        
        val sortedCategories = listOf(
            java.util.AbstractMap.SimpleEntry("Total", totalBlocks),
            java.util.AbstractMap.SimpleEntry("Ads", adsCount),
            java.util.AbstractMap.SimpleEntry("Trackers", trackersCount)
        )

        // =========================================================
        // TOTAL COUNT
        // =========================================================

        val displayCount =
            if (todayBlocks > 0) {
                todayBlocks
            } else {
                totalBlocks
            }



        // =========================================================
        // 4x1 WIDGET
        // =========================================================
        val showChart = true
        val showLegend = true

        views.setViewVisibility(
            R.id.widget_pie_chart,
            View.VISIBLE
        )

        // Legend container removed in new layout

        views.setViewVisibility(
            R.id.widget_empty_text,
            View.GONE
        )

        val chartBitmap = createCategoriesPieChartBitmap(
            context,
            sortedCategories,
            totalBlocks,
            68
        )

        views.setImageViewBitmap(
            R.id.widget_pie_chart,
            chartBitmap
        )

        // =========================================================
        // LEGEND
        //
        // 4x1 has limited vertical space.
        // Show only the top 3 categories.
        // =========================================================

        val rowLayouts =
            listOf(
                R.id.row_0,
                R.id.row_1,
                R.id.row_2,
            )

        val rowDots =
            listOf(
                R.id.row_0_dot,
                R.id.row_1_dot,
                R.id.row_2_dot,
            )

        val rowNames =
            listOf(
                R.id.row_0_name,
                R.id.row_1_name,
                R.id.row_2_name,
            )

        val rowCounts =
            listOf(
                R.id.row_0_count,
                R.id.row_1_count,
                R.id.row_2_count,
            )

        // Hide all rows first.
        for (id in rowLayouts) {
            views.setViewVisibility(
                id,
                View.GONE
            )
        }

        if (sortedCategories.isEmpty()) {

            views.setViewVisibility(
                R.id.widget_empty_text,
                View.VISIBLE
            )

        } else {

            views.setViewVisibility(
                R.id.widget_empty_text,
                View.GONE
            )

            // 4x1 = only 3 rows.
            val visibleRows =
                minOf(
                    3,
                    sortedCategories.size
                )

            for (i in 0 until visibleRows) {

                val category =
                    sortedCategories[i]

                val categoryName = category.key

                val percent =
                    if (totalBlocks > 0) {

                        (
                                category.value
                                    .toFloat()
                                    .div(totalBlocks)
                                    .times(100)
                                ).toInt()

                    } else {
                        0
                    }

                val categoryColor = listOf(Color.parseColor("#FF5F56"), Color.parseColor("#FFBD2E"), Color.parseColor("#27C93F")).getOrElse(i) { getColorForCategory(category.key) }

                views.setViewVisibility(
                    rowLayouts[i],
                    View.VISIBLE
                )

                views.setImageViewBitmap(
                    rowDots[i],
                    createDotBitmap(
                        categoryColor
                    )
                )

                views.setTextViewText(
                    rowCounts[i],
                    formatNumber(category.value)
                )

                views.setTextViewText(
                    rowNames[i],
                    categoryName
                )
            }
        }

        // =========================================================
        // CLICK → OPEN APP
        // =========================================================

        val appIntent =
            Intent(
                context,
                MainActivity::class.java
            )

        val appPendingIntent =
            PendingIntent.getActivity(
                context,
                1001,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        views.setOnClickPendingIntent(
            R.id.widget_root,
            appPendingIntent
        )

        // =========================================================
        // UPDATE
        // =========================================================

        appWidgetManager.updateAppWidget(
            appWidgetId,
            views
        )
    }

    // =============================================================
    // FORMAT NUMBERS
    // =============================================================

    private fun formatNumber(
        number: Int
    ): String {

        return when {

            number >= 1_000_000 ->
                String.format(
                    Locale.US,
                    "%.1fM",
                    number / 1_000_000f
                )

            number >= 1_000 ->
                String.format(
                    Locale.US,
                    "%,d",
                    number
                )

            else ->
                number.toString()
        }
    }

    // =============================================================
    // CATEGORY COLORS
    // =============================================================

    private fun getColorForCategory(
        category: String
    ): Int {

        return when (category.uppercase()) {

            "AD" ->
                Color.parseColor("#FF3B30")

            "TRACKER" ->
                Color.parseColor("#FF9500")

            "ANALYTICS" ->
                Color.parseColor("#007AFF")

            "MALWARE" ->
                Color.parseColor("#AF52DE")

            "TELEMETRY" ->
                Color.parseColor("#30D5C8")

            "SOCIAL" ->
                Color.parseColor("#FF2D55")

            "OTT" ->
                Color.parseColor("#00C7BE")

            "DOH" ->
                Color.parseColor("#5856D6")

            "MINER" ->
                Color.parseColor("#8E8E93")

            "SPAM" ->
                Color.parseColor("#BF5AF2")

            "OTHER" ->
                Color.parseColor("#34C759")

            else ->
                Color.parseColor("#34C759")
        }
    }

    // =============================================================
    // CATEGORY DOT
    // =============================================================

    private fun createDotBitmap(
        color: Int
    ): Bitmap {

        val size = 14

        val bitmap =
            Bitmap.createBitmap(
                size,
                size,
                Bitmap.Config.ARGB_8888
            )

        val canvas =
            Canvas(bitmap)

        val paint =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            ).apply {

                this.color = color
                style = Paint.Style.FILL
            }

        canvas.drawCircle(
            size / 2f,
            size / 2f,
            size / 2f,
            paint
        )

        return bitmap
    }

    // =============================================================
    // PIE CHART
    // =============================================================

    private fun createCategoriesPieChartBitmap(
        context: Context,
        categories: List<Map.Entry<String, Int>>,
        total: Int,
        sizeDp: Int
    ): Bitmap {
        val density = context.resources.displayMetrics.density
        val sizePx = (sizeDp * density).toInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val strokeWidth = sizePx * 0.14f
        val gap = strokeWidth * 0.4f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        if (total <= 0 || categories.isEmpty()) {
            paint.color = ContextCompat.getColor(context, R.color.widget_track_empty)
            val inset = strokeWidth / 2f
            canvas.drawArc(RectF(inset, inset, sizePx - inset, sizePx - inset), 0f, 360f, false, paint)
            return bitmap
        }

        val maxVal = total.toFloat().coerceAtLeast(1f)
        var currentRadius = (sizePx / 2f) - (strokeWidth / 2f)

        for (i in 0 until minOf(3, categories.size)) {
            val category = categories[i]
            val rect = RectF(
                (sizePx / 2f) - currentRadius,
                (sizePx / 2f) - currentRadius,
                (sizePx / 2f) + currentRadius,
                (sizePx / 2f) + currentRadius
            )
            
            val color = listOf(Color.parseColor("#FF5F56"), Color.parseColor("#FFBD2E"), Color.parseColor("#27C93F")).getOrElse(i) { getColorForCategory(category.key) }
            
            // Draw background track (lightened)
            paint.color = Color.argb(64, Color.red(color), Color.green(color), Color.blue(color))
            canvas.drawArc(rect, 0f, 360f, false, paint)
            
            // Draw foreground progress
            val sweep = if (category.value >= total && total > 0) 360f else (category.value / maxVal) * 340f + 5f
            paint.color = color
            canvas.drawArc(rect, -90f, sweep.coerceAtMost(360f), false, paint)
            
            currentRadius -= (strokeWidth + gap)
        }

        return bitmap
    }}
