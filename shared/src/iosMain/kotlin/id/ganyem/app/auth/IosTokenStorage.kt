package id.ganyem.app.auth

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.serialization.json.Json
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecItemNotFound
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosTokenStorage : TokenStorage {
    override suspend fun save(tokens: AppTokens) {
        clear()
        val bytes = Json.encodeToString(tokens).encodeToByteArray()
        val data = bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }
        val status = withQuery(data = data) { SecItemAdd(it, null) }
        check(status == 0) { "Gagal menyimpan token ke iOS Keychain (status $status)." }
    }

    override suspend fun load(): AppTokens? = memScoped {
        val result = alloc<CFTypeRefVar>()
        val status = withQuery(returnData = true) { SecItemCopyMatching(it, result.ptr) }
        if (status == errSecItemNotFound) return@memScoped null
        check(status == 0) { "Gagal membaca token dari iOS Keychain (status $status)." }
        val data = result.value?.let { platform.Foundation.CFBridgingRelease(it) as? NSData }
            ?: return@memScoped null
        val bytes = data.bytes?.reinterpret<kotlinx.cinterop.ByteVar>()
            ?.readBytes(data.length.toInt()) ?: ByteArray(0)
        Json.decodeFromString<AppTokens>(bytes.decodeToString())
    }

    override suspend fun clear() {
        val status = withQuery { SecItemDelete(it) }
        check(status == 0 || status == errSecItemNotFound) {
            "Gagal menghapus token dari iOS Keychain (status $status)."
        }
    }

    private inline fun <T> withQuery(
        data: NSData? = null,
        returnData: Boolean = false,
        operation: (CFDictionaryRef?) -> T
    ): T = memScoped {
        val service = platform.Foundation.CFBridgingRetain(SERVICE)!!
        val account = platform.Foundation.CFBridgingRetain(ACCOUNT)!!
        val valueData = data?.let { platform.Foundation.CFBridgingRetain(it) }
        val keys = mutableListOf(kSecClass, kSecAttrService, kSecAttrAccount)
        val values = mutableListOf<CFTypeRef?>(
            kSecClassGenericPassword,
            service,
            account
        )
        if (returnData) {
            keys += kSecReturnData
            values += kCFBooleanTrue
            keys += kSecMatchLimit
            values += kSecMatchLimitOne
        }
        if (valueData != null) {
            keys += kSecValueData
            values += valueData
        }

        val dictionary = CFDictionaryCreate(
            kCFAllocatorDefault,
            allocArrayOf(*keys.toTypedArray()).reinterpret(),
            allocArrayOf(*values.toTypedArray()).reinterpret(),
            keys.size.convert(),
            null,
            null
        )
        try {
            operation(dictionary)
        } finally {
            CFRelease(dictionary)
            CFRelease(service)
            CFRelease(account)
            valueData?.let(::CFRelease)
        }
    }

    private companion object {
        const val SERVICE = "id.ganyem.app.tokens"
        const val ACCOUNT = "seller"
    }
}
