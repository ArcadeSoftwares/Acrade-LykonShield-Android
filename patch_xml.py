import re

with open('app/src/main/res/layout/widget_shield.xml', 'r') as f:
    content = f.read()

# Large text (widget_total_count) gets negative tracking
content = re.sub(
    r'(android:id="@+id/widget_total_count".*?android:textSize="22sp")',
    r'\1\n            android:letterSpacing="-0.03"',
    content, flags=re.DOTALL
)

# Small all-caps text (widget_title) gets positive tracking
content = re.sub(
    r'(android:id="@+id/widget_title".*?android:textSize="10sp")',
    r'\1\n            android:letterSpacing="0.05"',
    content, flags=re.DOTALL
)

# Small labels (widget_total_label, row names)
content = re.sub(
    r'(android:textSize="9sp")',
    r'\1\n                android:letterSpacing="0.02"',
    content
)

with open('app/src/main/res/layout/widget_shield.xml', 'w') as f:
    f.write(content)
