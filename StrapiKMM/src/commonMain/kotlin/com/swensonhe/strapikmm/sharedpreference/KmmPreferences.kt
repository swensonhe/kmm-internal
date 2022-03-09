package com.swensonhe.strapikmm.sharedpreference

import com.liftric.kvault.KVault

class KmmPreference(private val kVault: KVault) {

    fun putInt(key: String, value: Int) {
        kVault.set(key, value)
    }

    fun getInt(key: String, default: Int): Int {
        return kVault.int(key) ?: default
    }

    fun putString(key: String, value: String) {
        kVault.set(key,value)
    }

    fun getString(key: String): String? {
        return kVault.string(key)
    }

    fun putDouble(key: String, value: Double) {
        kVault.set(key,value)
    }

    fun getDouble(key: String, default: Double): Double {
        return kVault.double(key) ?: default
    }

    fun putFloat(key: String, value: Float) {
        kVault.set(key,value)
    }

    fun getFloat(key: String, default: Float): Float {
        return kVault.float(key) ?: default
    }

    fun putLong(key: String, value: Long) {
        kVault.set(key,value)
    }

    fun getLong(key: String, default: Long): Long {
        return kVault.long(key) ?: default
    }

    fun putBool(key: String, value: Boolean) {
        kVault.set(key, value)
    }

    fun getBool(key: String, default: Boolean): Boolean {
        return kVault.bool(key) ?: default
    }

    fun contains(key: String): Boolean {
        return kVault.existsObject(key)
    }

    fun clear() {
        kVault.clear()
    }

    fun clearValue(key: String) {
        kVault.deleteObject(key)
    }
}