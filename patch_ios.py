import re

files = [
    'app/src/main/java/com/arcadesoftware/lykonshield/HomeScreen.kt',
    'app/src/main/java/com/arcadesoftware/lykonshield/BlockedScreen.kt',
    'app/src/main/java/com/arcadesoftware/lykonshield/ContentBlockingScreen.kt'
]

for file_path in files:
    with open(file_path, 'r') as f:
        content = f.read()

    # 1. Update all RoundedCornerShape(16.dp) to RoundedCornerShape(22.dp) for softer iOS-like corners
    content = re.sub(r'RoundedCornerShape\(16\.dp\)', 'RoundedCornerShape(22.dp)', content)
    
    # 2. Add negative letterSpacing to large titles (e.g. 34.sp)
    # Using regex to inject letterSpacing = (-1).sp, lineHeight = 36.sp
    content = re.sub(
        r'(fontSize = 34\.sp,\n\s*fontWeight = FontWeight\.Bold,)',
        r'\1\n                letterSpacing = (-1).sp,\n                lineHeight = 36.sp,',
        content
    )
    
    # 3. Adjust smaller ALL CAPS titles (like PROTECTION STATISTICS) for wider tracking
    content = re.sub(
        r'text = "([A-Z\s&]+)",\n(\s*)color = (.*?),\n(\s*)fontSize = (11|12|13)\.sp,\n(\s*)fontWeight = FontWeight\.Bold',
        r'text = "\1",\n\2color = \3,\n\4fontSize = \5.sp,\n\6fontWeight = FontWeight.SemiBold,\n\6letterSpacing = 0.5.sp',
        content
    )

    # 4. Modify 18.sp to have -0.5.sp tracking
    content = re.sub(
        r'fontSize = 18\.sp,(\s*)color = (.*?),?',
        r'fontSize = 18.sp,\1color = \2,\1letterSpacing = (-0.5).sp,',
        content
    )
    content = re.sub(
        r'fontSize = 18\.sp,\n(\s*)color = (.*?)\n',
        r'fontSize = 18.sp,\n\1color = \2,\n\1letterSpacing = (-0.5).sp,\n',
        content
    )

    with open(file_path, 'w') as f:
        f.write(content)

