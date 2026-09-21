import re

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    text = f.read()

# Change the text format to not exactly copy Samsung's "/ category"
text = text.replace('"/ $categoryName"', 'categoryName')

# Reduce the chart size from 96 to 68 to fit better
text = text.replace('totalBlocks,\n            96', 'totalBlocks,\n            68')
text = text.replace('totalBlocks,\n            54', 'totalBlocks,\n            68')

# Change chart background track to use the new theme color
# We need to import ContextCompat if not already there, but we can just use resources
import_string = "import androidx.core.content.ContextCompat\nimport android.graphics.RectF"
if "import androidx.core.content.ContextCompat" not in text:
    text = text.replace("import android.graphics.RectF", import_string)

text = re.sub(
    r'paint\.color = Color\.parseColor\("#E0E0E0"\)',
    r'paint.color = ContextCompat.getColor(context, R.color.widget_track_empty)',
    text
)

# Replace the inner ring track color (was argb(40, ...)) to be slightly more adaptive
# Instead of hardcoding 40 alpha, we'll keep the same color but it works well on light and dark.
# But let's make it 25% opacity (0x40)
text = text.replace(
    'paint.color = Color.argb(40, Color.red(color), Color.green(color), Color.blue(color))',
    'paint.color = Color.argb(64, Color.red(color), Color.green(color), Color.blue(color))'
)

# And let's adjust stroke size to look more elegant at 68dp
text = text.replace('val strokeWidth = sizePx * 0.12f', 'val strokeWidth = sizePx * 0.14f')

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(text)
