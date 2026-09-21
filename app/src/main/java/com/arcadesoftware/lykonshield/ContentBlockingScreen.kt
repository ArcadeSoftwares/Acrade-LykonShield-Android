package com.arcadesoftware.lykonshield

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Explicit
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    backdrop: Backdrop,
    onPopupStateChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE) }
    val isLightTheme = LocalIsLightTheme.current
    val contentColor = if (isLightTheme) Color.Black else Color.White
    val systemBlue = if (isLightTheme) Color(0xFF007AFF) else Color(0xFF0A84FF)


    var blockContentAlways by remember { mutableStateOf(prefs.getBoolean("block_content_always", true)) }
    var blockAdultContent by remember { mutableStateOf(prefs.getBoolean("block_adult_content", false)) }

    var blockedWebsites by remember {
        mutableStateOf(prefs.getStringSet("custom_blocked_websites", emptySet()) ?: emptySet())
    }

    var showAddWebsiteDialog by remember { mutableStateOf(false) }
    val dialogBackdrop = rememberLayerBackdrop()

    LaunchedEffect(showAddWebsiteDialog) {
        onPopupStateChange(showAddWebsiteDialog)
    }

    LaunchedEffect(blockContentAlways) {
        prefs.edit().putBoolean("block_content_always", blockContentAlways).apply()
    }
    LaunchedEffect(blockAdultContent) {
        prefs.edit().putBoolean("block_adult_content", blockAdultContent).apply()
    }
    LaunchedEffect(blockedWebsites) {
        prefs.edit().putStringSet("custom_blocked_websites", blockedWebsites).apply()
    }

    val isFilterActive = isProtectionEnabled || blockContentAlways

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(dialogBackdrop),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = state,
                modifier = Modifier
                    .fillMaxHeight()
                    ,
                contentPadding = PaddingValues(
                    top = topPadding,
                    bottom = bottomPadding + 24.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header
                item {
                    Column(
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Content Blocking",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Control distractions, adult websites, and custom domain filters",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            letterSpacing = (-0.2).sp
                        )
                    }
                }

                // Global Policy & Status Card
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "ENFORCEMENT POLICY",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                        GlassCard(
                            backdrop = backdrop,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                IosBlockingToggleRow(
                                    icon = Icons.Outlined.Shield,
                                    iconTint = Color(0xFF34C759),
                                    title = "Block Content 24/7",
                                    subtitle = "Keep content filters, YouTube Shorts limits & custom blocks active even when main shield is paused",
                                    checked = blockContentAlways,
                                    onCheckedChange = { blockContentAlways = it },
                                    backdrop = backdrop,
                                    showDivider = false
                                )
                            }
                        }
                    }
                }

                // Safety & Explicit Content Filters
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "PRIVACY & ADULT CONTENT",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(start = 12.dp)
                        )

                        GlassCard(
                            backdrop = backdrop,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                IosBlockingToggleRow(
                                    icon = Icons.Outlined.Explicit,
                                    iconTint = Color(0xFFAF52DE),
                                    title = "Adult & NSFW Websites",
                                    subtitle = "Blocks 500k+ known pornography and explicit web domains via DNS",
                                    checked = blockAdultContent && isFilterActive,
                                    enabled = isFilterActive,
                                    onCheckedChange = { blockAdultContent = it },
                                    backdrop = backdrop,
                                    showDivider = false
                                )
                            }
                        }
                    }
                }

                // Custom Website Blocklist Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CUSTOM WEBSITE BLOCKLIST (${blockedWebsites.size})",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "+ Add Website",
                                color = systemBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { showAddWebsiteDialog = true }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        if (blockedWebsites.isEmpty()) {
                            GlassCard(
                                backdrop = backdrop,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Language,
                                        contentDescription = null,
                                        tint = Color.Gray.copy(alpha = 0.6f),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = "No Custom Domains Blocked",
                                        color = contentColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Tap '+ Add Website' to restrict any domain across all installed applications.",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        } else {
                            GlassCard(
                                backdrop = backdrop,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    val sortedWebsites = remember(blockedWebsites) { blockedWebsites.sorted() }
                                    sortedWebsites.forEachIndexed { index, domain ->
                                        if (index > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(0.5.dp)
                                                    .padding(start = 58.dp)
                                                    .background(if (isLightTheme) Color(0xFFE5E5EA) else Color(0xFF38383A))
                                            )
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .clip(RoundedCornerShape(9.dp))
                                                        .background(Color(0xFFFF3B30).copy(alpha = 0.12f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Block,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFF3B30),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                    Text(
                                                        text = domain,
                                                        color = contentColor,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "Custom DNS Block",
                                                        color = Color.Gray,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .background(Color(0xFFFF3B30).copy(alpha = 0.12f))
                                                    .clickable {
                                                        blockedWebsites = blockedWebsites - domain
                                                    }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "Unblock",
                                                    color = Color(0xFFFF3B30),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = (-0.2).sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Website Dialog (Styled with IosBaseDialogCard)
        if (showAddWebsiteDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (isLightTheme) 0.15f else 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showAddWebsiteDialog = false }
                    ),
                contentAlignment = Alignment.Center
            ) {
                IosBaseDialogCard(
                    backdrop = rememberCombinedBackdrop(backdrop, dialogBackdrop),
                    modifier = Modifier
                        .width(320.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    AddWebsiteDialogContent(
                        onAdd = { newDomain ->
                            val clean = newDomain.trim().lowercase()
                                .removePrefix("http://")
                                .removePrefix("https://")
                                .substringBefore("/")
                                .substringBefore(":")
                            if (clean.isNotEmpty() && clean.contains(".")) {
                                blockedWebsites = blockedWebsites + clean
                                showAddWebsiteDialog = false
                            }
                        },
                        onDismiss = { showAddWebsiteDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun IosBlockingToggleRow(
    icon: ImageVector,
    iconTint: Color,
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
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(iconTint.copy(alpha = if (enabled) 0.15f else 0.06f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) iconTint else iconTint.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        color = effectiveColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = subtitle,
                        color = subtitleColor,
                        fontSize = 12.sp,
                        lineHeight = 15.sp
                    )
                }
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
                    .padding(start = 58.dp)
                    .background(if (isLightTheme) Color(0xFFE5E5EA) else Color(0xFF38383A))
            )
        }
    }
}

@Composable
private fun AddWebsiteDialogContent(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val isLightTheme = LocalIsLightTheme.current
    val textColor = if (isLightTheme) Color.Black else Color.White
    val systemBlue = if (isLightTheme) Color(0xFF007AFF) else Color(0xFF0A84FF)
    var domainInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

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
            Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                Text(
                    text = "Block Domain",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp
                )
                Text(
                    text = "Enter web domain to block (e.g. example.com)",
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
                    text = "domain.com",
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
                        if (domainInput.isNotBlank()) onAdd(domainInput)
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
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancel",
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val canAdd = domainInput.isNotBlank() && domainInput.contains(".")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (canAdd) Color(0xFFFF3B30) else Color(0xFFFF3B30).copy(alpha = 0.4f))
                    .clickable(enabled = canAdd) {
                        onAdd(domainInput)
                    }
                    .padding(vertical = 12.dp),
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
