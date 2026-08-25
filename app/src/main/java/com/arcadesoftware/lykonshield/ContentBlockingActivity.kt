package com.arcadesoftware.lykonshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arcadesoftware.lykonshield.ui.theme.LykonShieldTheme
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

class ContentBlockingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_LykonShield)
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("lykon_shield_prefs", MODE_PRIVATE)
        enableEdgeToEdge()

        setContent {
            val themeMode = remember { prefs.getInt("theme_mode", 0) }
            val isLiquidGlassEnabled = remember { prefs.getBoolean("liquid_glass_enabled", true) }
            val backdropBlurRadius = remember { prefs.getFloat("backdrop_blur_radius", 24f) }
            val isCustomShadersEnabled = remember { prefs.getBoolean("custom_shaders_enabled", true) }

            val isDark = when (themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            val view = androidx.compose.ui.platform.LocalView.current
            if (!view.isInEditMode) {
                androidx.compose.runtime.SideEffect {
                    val window = (view.context as android.app.Activity).window
                    androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
                    androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
                }
            }

            LykonShieldTheme(darkTheme = isDark, dynamicColor = false) {
                val isLightTheme = !isDark
                CompositionLocalProvider(
                    LocalIsLightTheme provides isLightTheme,
                    LocalIsLiquidGlassEnabled provides isLiquidGlassEnabled,
                    LocalBackdropBlurRadius provides backdropBlurRadius,
                    LocalIsCustomShadersEnabled provides isCustomShadersEnabled
                ) {
                    val backdrop = rememberLayerBackdrop {
                        drawRect(if (isLightTheme) Color(0xFFF2F2F7) else Color.Black)
                        drawContent()
                    }

                    val state = rememberLazyListState()
                    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isLightTheme) Color(0xFFF2F2F7) else Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .layerBackdrop(backdrop)
                        ) {
                            val isProtectionEnabled = prefs.getBoolean("protection_enabled", false)
                            ContentBlockingScreen(
                                state = state,
                                isProtectionEnabled = isProtectionEnabled,
                                topPadding = 12.dp + statusBarPadding,
                                bottomPadding = 88.dp + navBarPadding,
                                backdrop = backdrop
                            )
                        }
                    }
                }
            }
        }
    }
}
