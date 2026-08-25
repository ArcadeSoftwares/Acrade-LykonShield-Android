package com.arcadesoftware.lykonshield

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LykonAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var lastActionTime = 0L
    private var lastShortsActiveTimestamp = 0L
    private var lastNotifiedBlockTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        val prefs = getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)
        val isProtectionEnabled = prefs.getBoolean("protection_enabled", false)
        val blockContentAlways = prefs.getBoolean("block_content_always", true)
        
        // If Shield is disabled AND global persistent blocking is disabled, DO NOT block
        if (!isProtectionEnabled && !blockContentAlways) {
            return
        }

        val blockYtShorts = prefs.getBoolean("block_yt_shorts_in_app", false)
        val blockInstagramReels = prefs.getBoolean("block_instagram_reels_in_app", false)
        val dailyShortsLimitMinutes = prefs.getInt("daily_shorts_limit_minutes", 0)

        if (packageName == "com.google.android.youtube" || packageName == "com.google.android.youtube.tv") {
            if (blockYtShorts || dailyShortsLimitMinutes > 0) {
                checkAndHandleYouTubeShorts(event, prefs, blockYtShorts, dailyShortsLimitMinutes)
            }
        } else if (packageName == "com.instagram.android") {
            if (blockInstagramReels) {
                checkAndBlockInstagramReels(event)
            }
        }
    }

    private fun checkAndHandleYouTubeShorts(
        event: AccessibilityEvent,
        prefs: SharedPreferences,
        blockYtShorts: Boolean,
        dailyShortsLimitMinutes: Int
    ) {
        val rootNode = rootInActiveWindow ?: return

        try {
            val isShorts = isYouTubeShortsActive(rootNode)
            val now = System.currentTimeMillis()

            if (isShorts) {
                // If user configured a daily time limit for YouTube Shorts
                if (dailyShortsLimitMinutes > 0) {
                    val todayKey = "shorts_usage_" + SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    var currentUsageSec = prefs.getInt(todayKey, 0)

                    if (lastShortsActiveTimestamp > 0) {
                        val deltaSec = ((now - lastShortsActiveTimestamp) / 1000).toInt().coerceIn(0, 10)
                        if (deltaSec > 0) {
                            currentUsageSec += deltaSec
                            prefs.edit().putInt(todayKey, currentUsageSec).apply()
                        }
                    }
                    lastShortsActiveTimestamp = now

                    val limitSec = dailyShortsLimitMinutes * 60
                    if (currentUsageSec >= limitSec) {
                        if (now - lastActionTime >= 400) {
                            lastActionTime = now
                            performGlobalAction(GLOBAL_ACTION_BACK)
                            notifyShortsBlocked(isDailyLimit = true, limitMinutes = dailyShortsLimitMinutes)
                        }
                    }
                    return
                }

                // If immediate blocking is enabled
                if (blockYtShorts) {
                    if (now - lastActionTime >= 400) {
                        lastActionTime = now
                        performGlobalAction(GLOBAL_ACTION_BACK)
                        notifyShortsBlocked(isDailyLimit = false, limitMinutes = 0)
                    }
                }
            } else {
                lastShortsActiveTimestamp = 0L
            }
        } catch (e: Exception) {
            // Safe ignore
        }
    }

    private fun notifyShortsBlocked(isDailyLimit: Boolean, limitMinutes: Int) {
        handler.post {
            val msg = if (isDailyLimit) {
                "Daily Shorts limit ($limitMinutes min) reached! Auto-exited."
            } else {
                "YouTube Shorts blocked by Lykon Shield"
            }
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        }

        val now = System.currentTimeMillis()
        if (now - lastNotifiedBlockTime < 15_000) return
        lastNotifiedBlockTime = now

        val channelId = "lykon_content_blocking"
        val nm = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Content & Shorts Blocking",
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }

        val openSettingsIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "content_blocking")
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            201,
            openSettingsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isDailyLimit) "Daily Shorts Limit Reached" else "YouTube Shorts Blocked"
        val text = if (isDailyLimit) {
            "You have reached your daily allowance of $limitMinutes min. Tap to adjust limit."
        } else {
            "Shorts was auto-closed. Tap to configure Content Blocking."
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.dark_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        nm.notify(9001, notification)
    }

    private fun isYouTubeShortsActive(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        val text = node.text?.toString()?.lowercase() ?: ""
        val contentDescription = node.contentDescription?.toString()?.lowercase() ?: ""
        val className = node.className?.toString()?.lowercase() ?: ""

        // Robust match for YouTube Shorts active player, reels, and video containers
        if (viewId.contains("shorts_player") ||
            viewId.contains("reel_player") ||
            viewId.contains("reel_watch_fragment") ||
            viewId.contains("shorts_container") ||
            viewId.contains("reel_recycler") ||
            viewId.contains("shorts_video_player") ||
            viewId.contains("reel_watch") ||
            viewId.contains("shorts_watch") ||
            viewId.contains("reel_shelf") ||
            viewId.contains("shorts_shelf_renderer") ||
            viewId.contains("reel_adapter")
        ) {
            return true
        }

        // Active shorts tab in bottom navigation
        if (node.isSelected && (contentDescription.contains("shorts") || text == "shorts")) {
            return true
        }

        val childCount = node.childCount
        for (i in 0 until childCount) {
            val child = node.getChild(i) ?: continue
            if (isYouTubeShortsActive(child)) {
                return true
            }
        }

        return false
    }

    private fun checkAndBlockInstagramReels(event: AccessibilityEvent) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastActionTime < 350) return

        val rootNode = rootInActiveWindow ?: return
        try {
            val isReels = isInstagramReelsActive(rootNode)
            if (isReels) {
                lastActionTime = currentTime
                performGlobalAction(GLOBAL_ACTION_BACK)
                handler.post {
                    Toast.makeText(applicationContext, "Instagram Reels blocked by Lykon Shield", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            // Safe ignore
        }
    }

    private fun isInstagramReelsActive(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        val text = node.text?.toString()?.lowercase() ?: ""
        val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""

        if (viewId.contains("clips_viewer") || viewId.contains("reels_viewer") || viewId.contains("reel_viewer") || viewId.contains("clips_video_container")) {
            return true
        }
        if (node.isSelected && (contentDesc.contains("reels") || text == "reels")) {
            return true
        }

        val childCount = node.childCount
        for (i in 0 until childCount) {
            val child = node.getChild(i) ?: continue
            if (isInstagramReelsActive(child)) {
                return true
            }
        }
        return false
    }

    override fun onInterrupt() {}

    companion object {
        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val expectedComponentName = "${context.packageName}/${LykonAccessibilityService::class.java.canonicalName}"
            val enabledServices = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            val colonSplitter = android.text.TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServices)
            while (colonSplitter.hasNext()) {
                val componentName = colonSplitter.next()
                if (componentName.equals(expectedComponentName, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }

        fun openAccessibilitySettings(context: Context) {
            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
