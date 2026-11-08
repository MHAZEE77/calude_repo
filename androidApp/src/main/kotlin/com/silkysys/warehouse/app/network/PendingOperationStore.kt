package com.silkysys.warehouse.app.network

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.util.UUID


class PendingOperationStore(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val lock = Any()

    /** Returns the persisted operation ID for [fingerprint], creating and persisting one if needed. */
    fun keyFor(fingerprint: String): String = synchronized(lock) {
        val now = System.currentTimeMillis()
        pruneExpired(now)

        prefs.getString(fingerprint, null)
            ?.let { decode(it) }
            ?.let { return it.first }

        val key = UUID.randomUUID().toString()
        // commit() (not apply()) so the ID is on disk before the request leaves the device.
        prefs.edit().putString(fingerprint, "$key|$now").commit()
        key
    }

    /** Releases the operation ID once the outcome is definitive. */
    fun resolve(fingerprint: String) = synchronized(lock) {
        prefs.edit().remove(fingerprint).commit()
    }

    private fun pruneExpired(now: Long) {
        val all = prefs.all
        if (all.isEmpty()) return

        val stale = all.entries
            .filter { (_, value) -> (value as? String)?.let { decode(it) }?.second?.let { now - it > TTL_MS } ?: true }
            .map { it.key }
            .toMutableList()

        // Hard cap so a misbehaving backend can never grow this file without bound.
        val survivors = all.keys - stale.toSet()
        if (survivors.size > MAX_ENTRIES) {
            stale += survivors
                .sortedBy { (all[it] as? String)?.let { v -> decode(v)?.second } ?: 0L }
                .take(survivors.size - MAX_ENTRIES)
        }

        if (stale.isNotEmpty()) {
            prefs.edit().apply { stale.forEach { remove(it) } }.commit()
        }
    }

    private fun decode(raw: String): Pair<String, Long>? {
        val parts = raw.split('|')
        if (parts.size != 2) return null
        val ts = parts[1].toLongOrNull() ?: return null
        return parts[0] to ts
    }

    companion object {
        private const val PREFS_NAME = "pending_operation_ids"
        private const val TTL_MS = 24L * 60 * 60 * 1000
        private const val MAX_ENTRIES = 200

        fun fingerprint(method: String, url: String, body: ByteArray): String {
            val digest = MessageDigest.getInstance("SHA-256").apply {
                update(method.toByteArray())
                update(0)
                update(url.toByteArray())
                update(0)
                update(body)
            }
            return digest.digest().joinToString("") { "%02x".format(it) }
        }
    }
}
