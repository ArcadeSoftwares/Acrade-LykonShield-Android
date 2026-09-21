import os
import re

files = [
    'app/src/main/java/com/arcadesoftware/lykonshield/HomeScreen.kt',
    'app/src/main/java/com/arcadesoftware/lykonshield/BlockedScreen.kt',
    'app/src/main/java/com/arcadesoftware/lykonshield/ContentBlockingScreen.kt'
]

for file_path in files:
    with open(file_path, 'r') as f:
        content = f.read()

    # Find where letterSpacing is declared twice in a Text block
    # We will just do a simple replacement for the specific known cases.
    
    # In ContentBlockingScreen.kt
    content = content.replace(
        "lineHeight = 36.sp,\n                            color = contentColor,\n                            letterSpacing = (-0.5).sp",
        "lineHeight = 36.sp,\n                            color = contentColor"
    )
    content = content.replace(
        "lineHeight = 36.sp,\n                color = contentColor,\n                modifier = Modifier.padding(vertical = 12.dp),\n                letterSpacing = (-0.5).sp",
        "lineHeight = 36.sp,\n                color = contentColor,\n                modifier = Modifier.padding(vertical = 12.dp)"
    )
    
    # Just to be completely safe, we can use regex to remove a letterSpacing if it's followed soon by another letterSpacing in the same block, but it's risky.
    # Let's just fix the known duplicates.
    
    with open(file_path, 'w') as f:
        f.write(content)

