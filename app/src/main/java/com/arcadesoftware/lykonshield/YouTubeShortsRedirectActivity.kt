package com.arcadesoftware.lykonshield

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

/**
 * Intercepts YouTube Shorts URLs (e.g., https://www.youtube.com/shorts/VIDEO_ID or youtu.be/shorts/VIDEO_ID)
 * and either opens them in standard regular YouTube watch format (https://www.youtube.com/watch?v=VIDEO_ID)
 * or blocks/deflects them to avoid the addictive infinite Shorts scroll UI.
 */
class YouTubeShortsRedirectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
        finish()
    }

    private fun handleIntent(intent: Intent?) {
        val data: Uri = intent?.data ?: return
        val urlString = data.toString()

        val prefs = getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)
        val convertShortsToRegular = prefs.getBoolean("convert_yt_shorts_to_regular", true)
        val blockShortsEntirely = prefs.getBoolean("block_yt_shorts_completely", false)

        if (blockShortsEntirely) {
            Toast.makeText(this, "YouTube Shorts blocked by Lykon Shield", Toast.LENGTH_SHORT).show()
            return
        }

        // Extract Shorts video ID from URLs like /shorts/VIDEO_ID
        val path = data.path ?: ""
        val videoId = if (path.contains("/shorts/")) {
            path.substringAfter("/shorts/").substringBefore("/").substringBefore("?")
        } else {
            ""
        }

        if (videoId.isNotEmpty() && convertShortsToRegular) {
            val regularWatchUrl = "https://www.youtube.com/watch?v=$videoId"
            val watchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(regularWatchUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                // Find handlers excluding ourselves to prevent infinite loop
                val packageManager = packageManager
                val activities = packageManager.queryIntentActivities(watchIntent, 0)
                val targetActivity = activities.firstOrNull { it.activityInfo.packageName != packageName }
                if (targetActivity != null) {
                    watchIntent.setClassName(targetActivity.activityInfo.packageName, targetActivity.activityInfo.name)
                    startActivity(watchIntent)
                } else {
                    startActivity(Intent.createChooser(watchIntent, "Open standard video"))
                }
            } catch (e: Exception) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(regularWatchUrl))
                startActivity(browserIntent)
            }
        } else {
            // Forward as is to normal handler if disabled
            val forwardIntent = Intent(Intent.ACTION_VIEW, data)
            startActivity(forwardIntent)
        }
    }
}
