import re
with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'r') as f:
    text = f.read()

# Remove the first duplicate setTextViewText for rowNames
text = text.replace("""                views.setTextViewText(
                    rowNames[i],
                    categoryName
                )

                views.setTextViewText(
                    rowCounts[i],
                    formatNumber(category.value)
                )""", """                views.setTextViewText(
                    rowCounts[i],
                    formatNumber(category.value)
                )""")

with open('app/src/main/java/com/arcadesoftware/lykonshield/ShieldWidgetProvider.kt', 'w') as f:
    f.write(text)
