package com.arcadesoftware.lykonshield

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BlockActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val action = intent.action ?: return
        val domain = intent.getStringExtra("domain") ?: return

        val prefs = context.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)

        when (action) {
            "com.arcadesoftware.lykonshield.UNBLOCK_DOMAIN" -> {
                // 1. If it's in custom blocked websites, remove it
                val customBlocked = prefs.getStringSet("custom_blocked_websites", emptySet())?.toMutableSet() ?: mutableSetOf()
                if (customBlocked.remove(domain)) {
                    prefs.edit().putStringSet("custom_blocked_websites", customBlocked).apply()
                }

                // 2. Add to user whitelist / allowed domains
                val customAllowed = prefs.getStringSet("custom_allowed_domains", emptySet())?.toMutableSet() ?: mutableSetOf()
                customAllowed.add(domain)
                prefs.edit().putStringSet("custom_allowed_domains", customAllowed).apply()

                // Cancel notification
                val nm = context.getSystemService(NotificationManager::class.java)
                nm?.cancel(domain.hashCode())

                Toast.makeText(context, "Unblocked and removed from Shield: $domain", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
