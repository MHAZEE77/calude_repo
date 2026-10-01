package com.silkysys.warehouse.app.ui.utility

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.entity.UserEntity
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Stores session and warehouse data encrypted with an Android Keystore AES-GCM key.
 * Only the selected language remains in ordinary preferences. Passwords are never persisted.
 */
class SavePref(context: Context) {

    private val appContext = context.applicationContext
    private val publicPrefs: SharedPreferences =
        appContext.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE)
    private val legacyPrefs: SharedPreferences =
        appContext.getSharedPreferences(LEGACY_PREFS, Context.MODE_PRIVATE)
    private val securePrefs: SharedPreferences =
        appContext.getSharedPreferences(SECURE_PREFS, Context.MODE_PRIVATE)
    private val cipherStore = KeystoreCipherStore()

    init {
        migrateLegacyPreferences()
    }

    var language: String
        get() = publicPrefs.getString(KEY_LANGUAGE, Locale.getDefault().language).orEmpty()
        set(value) = publicPrefs.edit().putString(KEY_LANGUAGE, value).apply()

    var companyId: Int
        get() = getSecure(KEY_COMPANY_ID)?.toIntOrNull() ?: 0
        set(value) = putSecure(KEY_COMPANY_ID, value.toString())

    var refreshToken: String
        get() = getSecure(KEY_REFRESH_TOKEN).orEmpty()
        set(value) = putSecure(KEY_REFRESH_TOKEN, value)

    var clientId: String
        get() = getSecure(KEY_CLIENT_ID).orEmpty()
        set(value) = putSecure(KEY_CLIENT_ID, value)

    var authorization: String
        get() = getSecure(KEY_AUTHORIZATION).orEmpty()
        set(value) = putSecure(KEY_AUTHORIZATION, value.removePrefix("Bearer ").trim())

    var bearerAuthorization: String
        get() = authorization.takeIf { it.isNotBlank() }?.let { "Bearer $it" }.orEmpty()
        set(value) {
            authorization = value
        }

    var deviceToken: String
        get() = getSecure(KEY_DEVICE_TOKEN).orEmpty()
        set(value) = putSecure(KEY_DEVICE_TOKEN, value)

    var rememberedEmail: String
        get() = getSecure(KEY_REMEMBERED_EMAIL).orEmpty()
        set(value) = putSecure(KEY_REMEMBERED_EMAIL, value)

    var rememberEmail: Boolean
        get() = getSecure(KEY_REMEMBER_EMAIL)?.toBooleanStrictOrNull() ?: false
        set(value) = putSecure(KEY_REMEMBER_EMAIL, value.toString())

    var locationId: String?
        get() = getSecure(KEY_LOCATION_ID)?.takeIf { it.isNotBlank() }
        set(value) = putSecure(KEY_LOCATION_ID, value.orEmpty())

    var user: UserEntity?
        get() = getSecure(KEY_USER)?.let { encoded ->
            runCatching { json.decodeFromString(UserEntity.serializer(), encoded) }.getOrNull()
        }
        set(value) {
            if (value == null) {
                removeSecure(KEY_USER)
            } else {
                putSecure(KEY_USER, json.encodeToString(UserEntity.serializer(), value))
            }
        }

    fun hasActiveSession(): Boolean =
        authorization.isNotBlank() && refreshToken.isNotBlank() && clientId.isNotBlank() && companyId > 0

    fun clearSession() {
        val retainedDeviceToken = deviceToken
        val retainedEmail = rememberedEmail
        val shouldRememberEmail = rememberEmail
        securePrefs.edit().clear().apply()
        if (retainedDeviceToken.isNotBlank()) deviceToken = retainedDeviceToken
        if (shouldRememberEmail && retainedEmail.isNotBlank()) {
            rememberedEmail = retainedEmail
            rememberEmail = true
        }
    }

    fun clearAllData() {
        securePrefs.edit().clear().apply()
    }

    private fun putSecure(key: String, value: String) {
        if (value.isBlank()) {
            removeSecure(key)
            return
        }
        securePrefs.edit().putString(key, cipherStore.encrypt(value)).apply()
    }

    private fun getSecure(key: String): String? {
        val encrypted = securePrefs.getString(key, null) ?: return null
        return runCatching { cipherStore.decrypt(encrypted) }
            .onFailure { securePrefs.edit().remove(key).apply() }
            .getOrNull()
    }

    private fun removeSecure(key: String) {
        securePrefs.edit().remove(key).apply()
    }

    private fun migrateLegacyPreferences() {
        if (publicPrefs.getString(KEY_LANGUAGE, null).isNullOrBlank()) {
            legacyPrefs.getString(KEY_LANGUAGE, null)?.let {
                publicPrefs.edit().putString(KEY_LANGUAGE, it).apply()
            }
        }

        if (!securePrefs.getBoolean(KEY_MIGRATION_COMPLETE, false)) {
            legacyPrefs.getString(KEY_AUTHORIZATION, null)?.let { authorization = it }
            legacyPrefs.getString(KEY_REFRESH_TOKEN, null)?.let { refreshToken = it }
            legacyPrefs.getString(KEY_CLIENT_ID, null)?.let { clientId = it }
            legacyPrefs.getString(KEY_DEVICE_TOKEN, null)?.let { deviceToken = it }
            legacyPrefs.getString(KEY_EMAIL, null)?.takeIf { it.isNotBlank() }?.let {
                rememberedEmail = it
                rememberEmail = true
            }
            legacyPrefs.getString(KEY_LOCATION_ID, null)?.let { locationId = it }
            legacyPrefs.getString(KEY_LOCATIONS, null)?.let { putSecure(KEY_LOCATIONS, it) }
            legacyPrefs.getString(KEY_USER, null)?.let { putSecure(KEY_USER, it) }
            if (legacyPrefs.contains(KEY_COMPANY_ID)) companyId =
                legacyPrefs.getInt(KEY_COMPANY_ID, 0)

            securePrefs.edit().putBoolean(KEY_MIGRATION_COMPLETE, true).apply()
            legacyPrefs.edit().clear().apply()
        }
    }

    private class KeystoreCipherStore {
        private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        fun encrypt(value: String): String {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            val ciphertext = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))
            return listOf(cipher.iv, ciphertext)
                .joinToString(SEPARATOR) { Base64.encodeToString(it, Base64.NO_WRAP) }
        }

        fun decrypt(encoded: String): String {
            val parts = encoded.split(SEPARATOR, limit = 2)
            require(parts.size == 2) { "Invalid encrypted preference format" }
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val ciphertext = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
            return String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8)
        }

        private fun getOrCreateKey(): SecretKey {
            (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).run {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build(),
                )
                generateKey()
            }
        }
    }

    companion object {
        private const val PUBLIC_PREFS = "com.silkysys.warehouse.public"
        private const val SECURE_PREFS = "com.silkysys.warehouse.secure"
        private const val LEGACY_PREFS = "com.silkysys.warehouse.prefs"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_AUTHORIZATION = "authorization_key"
        private const val KEY_DEVICE_TOKEN = "device_token"
        private const val KEY_EMAIL = "email"
        private const val KEY_LOCATION_ID = "location_id_key"
        private const val KEY_COMPANY_ID = "companyId"
        private const val KEY_REFRESH_TOKEN = "refreshToken"
        private const val KEY_CLIENT_ID = "clientId"
        private const val KEY_LOCATIONS = "permitted_locations_list_key"
        private const val KEY_USER = "user_key"
        private const val KEY_REMEMBERED_EMAIL = "remembered_email"
        private const val KEY_REMEMBER_EMAIL = "remember_email"
        private const val KEY_MIGRATION_COMPLETE = "migration_complete_v2"

        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "silky_wms_secure_preferences_v2"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_BITS = 128
        private const val SEPARATOR = ":"

        private val json = Json { ignoreUnknownKeys = true }
    }
}
