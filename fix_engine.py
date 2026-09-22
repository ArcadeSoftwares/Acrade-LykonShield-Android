import re

with open("app/src/main/java/com/arcadesoftware/lykon/AdblockEngine.kt", "r") as f:
    content = f.read()

# 1. Change variable types
content = content.replace("private var blockedDomainHashes = LongArray(0)", "private var blockedDomains = HashSet<String>()")
content = content.replace("private var allowedDomainHashes = LongArray(0)", "private var allowedDomains = HashSet<String>()")

# 2. Fix initEngine race condition / array building
init_old = """        val tempBlocked = HashSet<String>()
        val tempAllowed = HashSet<String>()

        if (!loadCache(context, tempBlocked, tempAllowed)) {
            loadAllFilters(context, tempBlocked, tempAllowed)
            saveCache(context, tempBlocked, tempAllowed)
        }

        blockedDomainHashes = tempBlocked.map { fnv1a64(it) }.toLongArray()
        blockedDomainHashes.sort()
        allowedDomainHashes = tempAllowed.map { fnv1a64(it) }.toLongArray()
        allowedDomainHashes.sort()"""

init_new = """        val tempBlocked = HashSet<String>()
        val tempAllowed = HashSet<String>()

        if (!loadCache(context, tempBlocked, tempAllowed)) {
            loadAllFilters(context, tempBlocked, tempAllowed)
            saveCache(context, tempBlocked, tempAllowed)
        }

        blockedDomains = tempBlocked
        allowedDomains = tempAllowed"""
content = content.replace(init_old, init_new)

# 3. Fix reloadFilters race condition
reload_old = """    fun reloadFilters(context: Context) {
        Log.d(TAG, "Reloading filter lists...")
        val startTime = System.currentTimeMillis()

        blockedDomainHashes = LongArray(0)
        allowedDomainHashes = LongArray(0)
        loadedRuleCount.set(0)

        val tempBlocked = HashSet<String>()
        val tempAllowed = HashSet<String>()

        loadAllFilters(context, tempBlocked, tempAllowed)
        
        blockedDomainHashes = tempBlocked.map { fnv1a64(it) }.toLongArray()
        blockedDomainHashes.sort()
        allowedDomainHashes = tempAllowed.map { fnv1a64(it) }.toLongArray()
        allowedDomainHashes.sort()
        
        saveCache(context, tempBlocked, tempAllowed)"""

reload_new = """    fun reloadFilters(context: Context) {
        Log.d(TAG, "Reloading filter lists...")
        val startTime = System.currentTimeMillis()

        val tempBlocked = HashSet<String>()
        val tempAllowed = HashSet<String>()

        loadAllFilters(context, tempBlocked, tempAllowed)
        
        blockedDomains = tempBlocked
        allowedDomains = tempAllowed
        loadedRuleCount.set(tempBlocked.size)
        
        saveCache(context, tempBlocked, tempAllowed)"""
content = content.replace(reload_old, reload_new)

# 4. Fix isDomainBlocked and isDomainAllowed
is_blocked_old = """    private fun isDomainBlocked(domain: String): Boolean {
        var current = domain.lowercase()
        while (current.isNotEmpty()) {
            val hash = fnv1a64(current)
            if (java.util.Arrays.binarySearch(blockedDomainHashes, hash) >= 0) return true
            val dotIndex = current.indexOf('.')
            if (dotIndex == -1) break
            current = current.substring(dotIndex + 1)
            if (!current.contains('.')) break
        }
        return false
    }"""
is_blocked_new = """    private fun isDomainBlocked(domain: String): Boolean {
        var current = domain.lowercase()
        while (current.isNotEmpty()) {
            if (blockedDomains.contains(current)) return true
            val dotIndex = current.indexOf('.')
            if (dotIndex == -1) break
            current = current.substring(dotIndex + 1)
            if (!current.contains('.')) break
        }
        return false
    }"""
content = content.replace(is_blocked_old, is_blocked_new)

is_allowed_old = """    private fun isDomainAllowed(domain: String): Boolean {
        var current = domain.lowercase()
        while (current.isNotEmpty()) {
            val hash = fnv1a64(current)
            if (java.util.Arrays.binarySearch(allowedDomainHashes, hash) >= 0) return true
            val dotIndex = current.indexOf('.')
            if (dotIndex == -1) break
            current = current.substring(dotIndex + 1)
            if (!current.contains('.')) break
        }
        return false
    }"""
is_allowed_new = """    private fun isDomainAllowed(domain: String): Boolean {
        var current = domain.lowercase()
        while (current.isNotEmpty()) {
            if (allowedDomains.contains(current)) return true
            val dotIndex = current.indexOf('.')
            if (dotIndex == -1) break
            current = current.substring(dotIndex + 1)
            if (!current.contains('.')) break
        }
        return false
    }"""
content = content.replace(is_allowed_old, is_allowed_new)

with open("app/src/main/java/com/arcadesoftware/lykon/AdblockEngine.kt", "w") as f:
    f.write(content)

