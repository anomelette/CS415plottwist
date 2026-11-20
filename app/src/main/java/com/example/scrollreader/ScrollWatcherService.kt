package com.example.scrollreader

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.accessibility.AccessibilityEvent
import kotlin.math.roundToInt

class ScrollWatcherService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var foregroundPackage: String? = null
    private lateinit var prefs: Prefs

    private val tick = object : Runnable {
        override fun run() {
            onTick()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onServiceConnected() {
        prefs = Prefs(this)
        prefs.addLog("Scroll watcher turned on")
        handler.post(tick)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        if (Config.isIgnored(pkg)) return
        foregroundPackage = pkg
    }

    private fun onTick() {
        val power = getSystemService(POWER_SERVICE) as PowerManager
        if (!power.isInteractive) return            // screen off: not scrolling
        if (ReadingActivity.isShowing) return        // already reading
        val pkg = foregroundPackage ?: return
        val appName = Config.TARGET_APPS[pkg] ?: return  // not a social app

        // They left a reading break early and came back to scrolling: send them back.
        if (prefs.owedReadSeconds > 0) {
            prefs.addLog("Back on $appName with ${Config.formatSeconds(prefs.owedReadSeconds)} reading still owed — reopened break")
            openReading()
            return
        }

        val scrolled = prefs.scrollSeconds + 1
        prefs.scrollSeconds = scrolled
        if (scrolled >= prefs.scrollLimitSeconds) {
            val owed = (scrolled * prefs.readRatio).roundToInt().coerceIn(1, Config.MAX_READ_SECONDS)
            prefs.scrollSeconds = 0
            prefs.owedReadSeconds = owed
            prefs.addLog("Interrupted after ${Config.formatSeconds(scrolled)} on $appName → ${Config.formatSeconds(owed)} reading owed")
            openReading()
        }
    }

    private fun openReading() {
        // Accessibility services are allowed to start activities from the background.
        startActivity(
            Intent(this, ReadingActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        super.onDestroy()
    }
}
