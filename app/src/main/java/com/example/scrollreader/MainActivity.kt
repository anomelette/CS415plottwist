package com.example.scrollreader

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cs415plottwist.R

/** Control panel: turn the watcher on, change settings, test, and read/share the log. */
class MainActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs
    private lateinit var statusText: TextView
    private lateinit var enableButton: Button
    private lateinit var progressText: TextView
    private lateinit var likedText: TextView
    private lateinit var limitInput: EditText
    private lateinit var ratioInput: EditText
    private lateinit var logText: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val refresher = object : Runnable {
        override fun run() {
            refresh()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Prefs(this)

        statusText = findViewById(R.id.statusText)
        enableButton = findViewById(R.id.enableButton)
        progressText = findViewById(R.id.progressText)
        likedText = findViewById(R.id.likedText)
        limitInput = findViewById(R.id.limitInput)
        ratioInput = findViewById(R.id.ratioInput)
        logText = findViewById(R.id.logText)

        limitInput.setText(prefs.scrollLimitSeconds.toString())
        ratioInput.setText(prefs.readRatio.toString())

        enableButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val limit = limitInput.text.toString().toIntOrNull()
            val ratio = ratioInput.text.toString().toFloatOrNull()
            if (limit == null || limit < 5 || ratio == null || ratio <= 0f) {
                Toast.makeText(this, "Limit must be at least 5 seconds; ratio above 0", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            prefs.scrollLimitSeconds = limit
            prefs.readRatio = ratio
            prefs.addLog("Settings: break after ${limit}s scrolling, $ratio s reading per scroll second")
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.testButton).setOnClickListener {
            prefs.owedReadSeconds = Config.TEST_READ_SECONDS
            prefs.addLog("Test interruption")
            startActivity(Intent(this, ReadingActivity::class.java))
        }

        findViewById<Button>(R.id.resetBookButton).setOnClickListener {
            prefs.likedBookId = null
            prefs.passageIndex = 0
            prefs.addLog("Reset picked book")
        }

        findViewById<Button>(R.id.shareLogButton).setOnClickListener {
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Scroll Reader log")
                putExtra(Intent.EXTRA_TEXT, prefs.log)
            }
            startActivity(Intent.createChooser(send, "Share log"))
        }

        findViewById<Button>(R.id.clearLogButton).setOnClickListener { prefs.clearLog() }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refresher)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refresher)
    }

    private fun isWatcherEnabled(): Boolean {
        val enabled = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.contains("$packageName/${ScrollWatcherService::class.java.name}") ||
            enabled.contains("$packageName/.ScrollWatcherService")
    }

    private fun refresh() {
        if (isWatcherEnabled()) {
            statusText.text = "Scroll watcher is ON"
            enableButton.text = "Accessibility settings"
        } else {
            statusText.text = "Scroll watcher is OFF"
            enableButton.text = "Turn it on (Accessibility → Scroll Reader)"
        }
        progressText.text =
            "Scrolling since last break: ${Config.formatSeconds(prefs.scrollSeconds)} of ${Config.formatSeconds(prefs.scrollLimitSeconds)}\n" +
                "Reading owed: ${Config.formatSeconds(prefs.owedReadSeconds)}"
        val liked = Books.byId(prefs.likedBookId)
        likedText.text = if (liked != null) {
            "Your book: ${liked.title} — next is part ${prefs.passageIndex % liked.passages.size + 1} of ${liked.passages.size}"
        } else {
            "No book picked yet. Breaks show random excerpts until you pick one."
        }
        logText.text = prefs.log.ifEmpty { "Nothing logged yet." }
    }
}
