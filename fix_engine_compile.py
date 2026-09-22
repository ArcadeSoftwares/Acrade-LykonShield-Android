import re

with open("app/src/main/java/com/arcadesoftware/lykon/AdblockEngine.kt", "r") as f:
    content = f.read()

content = content.replace("blockedDomainHashes = tempBlocked.map { fnv1a64(it) }.toLongArray()", "blockedDomains = tempBlocked")
content = content.replace("blockedDomainHashes.sort()", "")
content = content.replace("allowedDomainHashes = tempAllowed.map { fnv1a64(it) }.toLongArray()", "allowedDomains = tempAllowed")
content = content.replace("allowedDomainHashes.sort()", "")
content = content.replace("blockedDomainHashes", "blockedDomains")
content = content.replace("allowedDomainHashes", "allowedDomains")

with open("app/src/main/java/com/arcadesoftware/lykon/AdblockEngine.kt", "w") as f:
    f.write(content)
