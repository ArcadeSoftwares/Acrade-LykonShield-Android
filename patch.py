import re

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    content = f.read()

# Remove anything that resembles the old 4x1 comment block and the pie chart block, and replace it
# We will just find the block from "// 4x1 WIDGET" down to "views.setImageViewBitmap(" and replace it.

start_marker = "// ========================================================="
content = re.sub(r'// =========================================================\s*// 4x1 WIDGET.*?views\.setImageViewBitmap\(\s*R\.id\.widget_pie_chart,\s*chartBitmap\s*\)', 
"""// =========================================================
        // 4x1 WIDGET
        // =========================================================
        val showChart = true
        val showLegend = true

        views.setViewVisibility(
            R.id.widget_pie_chart,
            View.VISIBLE
        )

        views.setViewVisibility(
            R.id.widget_legend_container,
            View.VISIBLE
        )

        views.setViewVisibility(
            R.id.widget_empty_text,
            View.GONE
        )

        val chartBitmap = createCategoriesPieChartBitmap(
            context,
            sortedCategories,
            totalBlocks,
            54
        )

        views.setImageViewBitmap(
            R.id.widget_pie_chart,
            chartBitmap
        )""", content, flags=re.DOTALL)

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(content)

