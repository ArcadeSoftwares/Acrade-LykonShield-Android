#!/bin/bash
sed -i '/if (showCacheDialog)/i \
        val context = androidx.compose.ui.platform.LocalContext.current \
        if (showUpdateConfirmDialog) { \
            Box( \
                modifier = Modifier.fillMaxSize(), \
                contentAlignment = Alignment.Center \
            ) { \
                Box( \
                    modifier = Modifier \
                        .fillMaxSize() \
                        .layerBackdrop(dialogBackdrop) \
                        .background(Color.Black.copy(alpha = if (isLightTheme) 0.08f else 0.3f)) \
                        .clickable( \
                            interactionSource = remember { MutableInteractionSource() }, \
                            indication = null, \
                            onClick = { showUpdateConfirmDialog = false } \
                        ) \
                ) \
                LykonConfirmDialog( \
                    title = "This will update the filter lists from Brave.", \
                    confirmText = "Update", \
                    cancelText = "Cancel", \
                    onConfirm = { \
                        showUpdateConfirmDialog = false \
                        com.arcadesoftware.lykonshield.FilterListUpdater.checkAndUpdate(context, true) { success -> \
                            if (success) { \
                                android.widget.Toast.makeText(context, "Filter lists updated successfully.", android.widget.Toast.LENGTH_SHORT).show() \
                            } else { \
                                android.widget.Toast.makeText(context, "Failed to update filter lists.", android.widget.Toast.LENGTH_SHORT).show() \
                            } \
                        } \
                    }, \
                    onDismiss = { showUpdateConfirmDialog = false }, \
                    backdrop = rememberCombinedBackdrop(backdrop, dialogBackdrop) \
                ) \
            } \
        }\n' app/src/main/java/com/arcadesoftware/lykonshield/DeveloperScreen.kt
