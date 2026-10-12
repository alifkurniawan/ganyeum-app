package id.ganyem.app.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.serialization.json.Json
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


class AndroidTokenStorage(
    context: Context
) : TokenStorage {
    private val preferences = context.applicationContext
        .getSharedPreferences("ganyem_secure_tokens", Context.MODE_PRIVATE)

    private val json = Json


    override suspend fun save(tokens: AppTokens) {
        val plaintext = json.encodeToString(tokens)
            .toByteArray(Charsets.UTF_8)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())

        val ciphertext = cipher.doFinal(plaintext)

        val saved = preferences.edit()
            .putString("ciphertext", Base64.encodeToString(ciphertext, Base64.NO_WRAP))
            .putString("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .commit()

        check(saved) { "Gagal menyimpan token." }
    }

    override suspend fun load(): AppTokens? {
        val ciphertextValue = preferences.getString("ciphertext", null)
            ?: return null
        val ivValue = preferences.getString("iv", null)
            ?: return null

        val ciphertext = Base64.decode(ciphertextValue, Base64.NO_WRAP)
        val iv = Base64.decode(ivValue, Base64.NO_WRAP)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateKey(),
            GCMParameterSpec(128, iv)
        )

        val plaintext = cipher.doFinal(ciphertext)
            .toString(Charsets.UTF_8)

        return json.decodeFromString<AppTokens>(plaintext)
    }

    override suspend fun clear() {
        check(preferences.edit().clear().commit()) {
            "Gagal menghapus token."
        }
    }


    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) {
            return existingKey
        }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )

        return generator.generateKey()
    }

    private companion object {
        const val KEY_ALIAS = "ganyem_access_token_key"
    }

}