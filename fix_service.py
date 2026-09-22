import re

with open("app/src/main/java/com/arcadesoftware/lykonshield/LykonVpnService.kt", "r") as f:
    content = f.read()

# 1. Executors
content = content.replace("Executors.newCachedThreadPool()", "Executors.newFixedThreadPool(4)")

# 2. Finally block isVpnActive = false
finally_old = """        } catch (e: Exception) {
            if (isRunning) Log.e(TAG, "Error in VPN Loop", e)
        } finally {
            inputStream.close()
            outputStream.close()
        }"""
finally_new = """        } catch (e: Exception) {
            if (isRunning) Log.e(TAG, "Error in VPN Loop", e)
        } finally {
            isVpnActive = false
            inputStream.close()
            outputStream.close()
        }"""
content = content.replace(finally_old, finally_new)

# 3. Preferences caching
cache_setup = """    private var dnsExecutor: ExecutorService? = null
    private val outputLock = Any()

    // Cached preferences
    private var cachedExcludedApps: Set<String> = emptySet()
    private var cachedCustomAllowedDomains: Set<String> = emptySet()
    private var cachedProtectionLevel: String = "TRACKER_AND_ADS"
    private var cachedBlockContentAlways: Boolean = true
    private var cachedBlockAdultContent: Boolean = false
    private var cachedCustomBlockedWebsites: Set<String> = emptySet()
    private var prefsListener: android.content.SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun onCreate() {
        super.onCreate()
        dnsExecutor = Executors.newFixedThreadPool(4)
        
        val prefs = applicationContext.getSharedPreferences("lykon_shield_prefs", Context.MODE_PRIVATE)
        
        fun updateCachedPrefs() {
            cachedExcludedApps = prefs.getStringSet("excluded_apps", emptySet())?.toSet() ?: emptySet()
            cachedCustomAllowedDomains = prefs.getStringSet("custom_allowed_domains", emptySet())?.toSet() ?: emptySet()
            cachedProtectionLevel = prefs.getString("protection_level", "TRACKER_AND_ADS") ?: "TRACKER_AND_ADS"
            cachedBlockContentAlways = prefs.getBoolean("block_content_always", true)
            cachedBlockAdultContent = prefs.getBoolean("block_adult_content", false)
            cachedCustomBlockedWebsites = prefs.getStringSet("custom_blocked_websites", emptySet())?.toSet() ?: emptySet()
        }
        updateCachedPrefs()
        
        prefsListener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> updateCachedPrefs() }
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
    }"""

content = re.sub(r'    private var dnsExecutor: ExecutorService\? = null\n    private val outputLock = Any\(\)\n\n    override fun onCreate\(\) \{\n        super\.onCreate\(\)\n        dnsExecutor = Executors\.newFixedThreadPool\(4\)\n    \}', cache_setup, content, flags=re.DOTALL)

# 4. Use cached prefs in handleDnsQuery
dns_query_old = """        val prefs = applicationContext.getSharedPreferences("lykon_shield_prefs", android.content.Context.MODE_PRIVATE)
        val excludedApps = prefs.getStringSet("excluded_apps", emptySet()) ?: emptySet()
        if (packageName in excludedApps) {
            forwardDnsQuery(requestPacket, ipHeaderLen, udpHeaderLen, outputStream)
            return
        }

        val customAllowedDomains = prefs.getStringSet("custom_allowed_domains", emptySet()) ?: emptySet()
        val lowerDomain = domain.lowercase()

        // If user explicitly unblocked/whitelisted this domain, always forward
        if (customAllowedDomains.any { lowerDomain == it || lowerDomain.endsWith(".$it") }) {
            forwardDnsQuery(requestPacket, ipHeaderLen, udpHeaderLen, outputStream)
            ShieldStatsManager.recordTraffic(applicationContext, domain, packageName, isBlocked = false)
            return
        }

        val protectionLevel = prefs.getString("protection_level", "TRACKER_AND_ADS") ?: "TRACKER_AND_ADS"
        val isShieldOn = isVpnActive && protectionLevel != "DISABLED"

        val blockContentAlways = prefs.getBoolean("block_content_always", true)
        val blockAdultContent = prefs.getBoolean("block_adult_content", false)
        val customBlockedWebsites = prefs.getStringSet("custom_blocked_websites", emptySet()) ?: emptySet()"""

dns_query_new = """        if (packageName in cachedExcludedApps) {
            forwardDnsQuery(requestPacket, ipHeaderLen, udpHeaderLen, outputStream)
            return
        }

        val lowerDomain = domain.lowercase()
        // If user explicitly unblocked/whitelisted this domain, always forward
        if (cachedCustomAllowedDomains.any { lowerDomain == it || lowerDomain.endsWith(".$it") }) {
            forwardDnsQuery(requestPacket, ipHeaderLen, udpHeaderLen, outputStream)
            ShieldStatsManager.recordTraffic(applicationContext, domain, packageName, isBlocked = false)
            return
        }

        val isShieldOn = isVpnActive && cachedProtectionLevel != "DISABLED"
        val blockContentAlways = cachedBlockContentAlways
        val blockAdultContent = cachedBlockAdultContent
        val customBlockedWebsites = cachedCustomBlockedWebsites"""

content = content.replace(dns_query_old, dns_query_new)

with open("app/src/main/java/com/arcadesoftware/lykonshield/LykonVpnService.kt", "w") as f:
    f.write(content)

