package com.arcadesoftware.lykonshield

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidToggle

@Composable
fun ContentBlockingScreen(
    state: LazyListState,
    isProtectionEnabled: Boolean,
    topPadding: Dp,
    bottomPadding: Dp,
    backdrop: Backdrop
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Blank page as requested
    }
}

@Composable
private fun BlockingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    backdrop: Backdrop,
    showDivider: Boolean,
    enabled: Boolean = true
) {
    val isLightTheme = LocalIsLightTheme.current
    val contentColor = if (isLightTheme) Color.Black else Color.White
    val effectiveColor = if (enabled) contentColor else contentColor.copy(alpha = 0.38f)
    val subtitleColor = if (enabled) Color.Gray else Color.Gray.copy(alpha = 0.38f)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    color = effectiveColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontSize = 12.sp,
                    lineHeight = 15.sp
                )
            }

            if (enabled) {
                LiquidToggle(
                    selected = { checked },
                    onSelect = onCheckedChange,
                    backdrop = backdrop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp, 20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isLightTheme) Color.LightGray.copy(alpha = 0.3f) else Color.DarkGray.copy(alpha = 0.3f))
                )
            }
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .padding(start = 16.dp)
                    .background(if (isLightTheme) Color(0xFFC7C7CC) else Color(0xFF38383A))
            )
        }
    }
}

@Composable
fun AddWebsiteDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit,
    backdrop: Backdrop
) {
    val isLightTheme = LocalIsLightTheme.current
    val textColor = if (isLightTheme) Color.Black else Color.White
    val systemBlue = if (isLightTheme) Color(0xFF007AFF) else Color(0xFF0A84FF)
    var domainInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    GlassCard(
        backdrop = backdrop,
        modifier = Modifier.width(320.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(1.dp), modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Block Website / Domain",
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Enter domain to block (e.g. reddit.com)",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isLightTheme) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.1f))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        color = textColor.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isLightTheme) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                if (domainInput.isEmpty()) {
                    Text(
                        text = "example.com",
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                }
                BasicTextField(
                    value = domainInput,
                    onValueChange = { domainInput = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(if (isLightTheme) Color.Black else Color.White),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onAdd(domainInput)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isLightTheme) Color.Black.copy(0.06f) else Color.White.copy(0.1f))
                        .clickable { onDismiss() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel",
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (domainInput.isNotBlank()) systemBlue else systemBlue.copy(alpha = 0.4f))
                        .clickable(enabled = domainInput.isNotBlank()) {
                            onAdd(domainInput)
                        }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Block",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// SF Symbol style: slider.horizontal.3 for Content Blocking
val ContentBlockingIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ContentBlocking",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(4f, 6f)
        lineTo(20f, 6f)
        moveTo(4f, 12f)
        lineTo(20f, 12f)
        moveTo(4f, 18f)
        lineTo(20f, 18f)
        moveTo(8f, 3.5f)
        lineTo(8f, 8.5f)
        moveTo(16f, 9.5f)
        lineTo(16f, 14.5f)
        moveTo(10f, 15.5f)
        lineTo(10f, 20.5f)
    }.build()

val ContentBlockingFilledIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ContentBlockingFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
            strokeLineWidth = 2.2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            moveTo(4f, 6f)
            lineTo(20f, 6f)
            moveTo(4f, 12f)
            lineTo(20f, 12f)
            moveTo(4f, 18f)
            lineTo(20f, 18f)
        }
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(8f, 3.5f)
            curveTo(6.89f, 3.5f, 6f, 4.39f, 6f, 5.5f)
            verticalLineTo(6.5f)
            curveTo(6f, 7.61f, 6.89f, 8.5f, 8f, 8.5f)
            curveTo(9.11f, 8.5f, 10f, 7.61f, 10f, 6.5f)
            verticalLineTo(5.5f)
            curveTo(10f, 4.39f, 9.11f, 3.5f, 8f, 3.5f)
            close()

            moveTo(16f, 9.5f)
            curveTo(14.89f, 9.5f, 14f, 10.39f, 14f, 11.5f)
            verticalLineTo(12.5f)
            curveTo(14f, 13.61f, 14.89f, 14.5f, 16f, 14.5f)
            curveTo(17.11f, 14.5f, 18f, 13.61f, 18f, 12.5f)
            verticalLineTo(11.5f)
            curveTo(18f, 10.39f, 17.11f, 9.5f, 16f, 9.5f)
            close()

            moveTo(10f, 15.5f)
            curveTo(8.89f, 15.5f, 8f, 16.39f, 8f, 17.5f)
            verticalLineTo(18.5f)
            curveTo(8f, 19.61f, 8.89f, 20.5f, 10f, 20.5f)
            curveTo(11.11f, 20.5f, 12f, 19.61f, 12f, 18.5f)
            verticalLineTo(17.5f)
            curveTo(12f, 16.39f, 11.11f, 15.5f, 10f, 15.5f)
            close()
        }
    }.build()
