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

    # Rounded corners: 16.dp to 20.dp or 22.dp (closer to iOS smooth corners)
    content = re.sub(r'RoundedCornerShape\(16\.dp\)', 'RoundedCornerShape(20.dp)', content)

    # 34.sp titles (Large Title) -> tighter tracking
    content = re.sub(
        r'fontSize = 34\.sp,\n(\s*)fontWeight = FontWeight\.Bold,',
        r'fontSize = 34.sp,\n\1fontWeight = FontWeight.Bold,\n\1letterSpacing = (-1).sp,',
        content
    )

    # Smaller ALL CAPS labels (e.g. 11.sp, 12.sp, 13.sp) -> looser tracking
    content = re.sub(
        r'text = "([A-Z\s&]+)",\n(\s*)color = Color\.Gray,\n(\s*)fontSize = (11|12|13)\.sp,\n(\s*)fontWeight = FontWeight\.Bold',
        r'text = "\1",\n\2color = Color.Gray,\n\3fontSize = \4.sp,\n\5fontWeight = FontWeight.SemiBold,\n\5letterSpacing = 0.5.sp',
        content
    )

    # Section Headers (like 20.sp / 22.sp / 18.sp)
    content = re.sub(
        r'fontSize = 18\.sp,(\s*)color = (.*?)\)',
        r'fontSize = 18.sp,\1color = \2,\1letterSpacing = (-0.3).sp)',
        content
    )
    content = re.sub(
        r'fontSize = 18\.sp,\n(\s*)color = (.*?)\n',
        r'fontSize = 18.sp,\n\1color = \2,\n\1letterSpacing = (-0.3).sp,\n',
        content
    )

    # Spacing and Divider softening
    content = re.sub(
        r'height\(0\.5\.dp\)',
        r'height(0.5.dp)',
        content
    )

    with open(file_path, 'w') as f:
        f.write(content)

