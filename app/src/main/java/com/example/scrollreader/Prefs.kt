package com.example.scrollreader

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Small wrapper around SharedPreferences so the service and screens share state. */
class Prefs(context: Context) {
    private val sp = context.applicationContext
        .getSharedPreferences("scrollreader", Context.MODE_PRIVATE)

    var scrollSeconds: Int
        get() = sp.getInt("scrollSeconds", 0)
        set(v) = sp.edit().putInt("scrollSeconds", v).apply()

    var owedReadSeconds: Int
        get() = sp.getInt("owedReadSeconds", 0)
        set(v) = sp.edit().putInt("owedReadSeconds", v).apply()

    var scrollLimitSeconds: Int
        get() = sp.getInt("scrollLimitSeconds", Config.DEFAULT_SCROLL_LIMIT_SECONDS)
        set(v) = sp.edit().putInt("scrollLimitSeconds", v).apply()

    var readRatio: Float
        get() = sp.getFloat("readRatio", Config.DEFAULT_READ_RATIO)
        set(v) = sp.edit().putFloat("readRatio", v).apply()

    var likedBookId: String?
        get() = sp.getString("likedBookId", null)
        set(v) = sp.edit().putString("likedBookId", v).apply()

    var passageIndex: Int
        get() = sp.getInt("passageIndex", 0)
        set(v) = sp.edit().putInt("passageIndex", v).apply()

    /** Timestamped log, newest first. Use it as data for the experience prototype. */
    fun addLog(message: String) {
        val stamp = SimpleDateFormat("MMM d HH:mm:ss", Locale.US).format(Date())
        val updated = "$stamp  $message\n" + (sp.getString("log", "") ?: "")
        sp.edit().putString("log", updated.take(20_000)).apply()
    }

    val log: String
        get() = sp.getString("log", "") ?: ""

    fun clearLog() = sp.edit().remove("log").apply()
}
