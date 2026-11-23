package com.example.scrollreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.cs415plottwist.R

/**
 * The reading break. Shows an excerpt and counts down the reading owed.
 * The countdown only runs while this screen is visible, so leaving doesn't "pay off" the time.
 */
class ReadingActivity : AppCompatActivity() {

    companion object {
        @Volatile
        var isShowing = false
    }

    private lateinit var prefs: Prefs
    private lateinit var book: Book
    private var passageIndex = 0
    private var discovering = true  // true = no book picked yet, showing random excerpts

    private lateinit var titleText: TextView
    private lateinit var authorText: TextView
    private lateinit var passageText: TextView
    private lateinit var timerText: TextView
    private lateinit var likeButton: Button
    private lateinit var otherButton: Button
    private lateinit var openButton: Button
    private lateinit var doneButton: Button

    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            val owed = prefs.owedReadSeconds
            if (owed > 0) prefs.owedReadSeconds = owed - 1
            updateTimer()
            if (prefs.owedReadSeconds > 0) handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)
        prefs = Prefs(this)

        titleText = findViewById(R.id.bookTitle)
        authorText = findViewById(R.id.bookAuthor)
        passageText = findViewById(R.id.passageText)
        timerText = findViewById(R.id.timerText)
        likeButton = findViewById(R.id.likeButton)
        otherButton = findViewById(R.id.otherButton)
        openButton = findViewById(R.id.openButton)
        doneButton = findViewById(R.id.doneButton)

        pickPassage()
        showPassage()

        likeButton.setOnClickListener {
            prefs.likedBookId = book.id
            prefs.passageIndex = passageIndex
            discovering = false
            prefs.addLog("Picked a book: ${book.title}")
            showPassage()
        }
        otherButton.setOnClickListener {
            prefs.addLog("Skipped: ${book.title}")
            book = Books.all.filter { it.id != book.id }.random()
            passageIndex = 0
            showPassage()
        }
        openButton.setOnClickListener {
            prefs.addLog("Opened full book: ${book.title}")
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(book.fullBookUrl)))
        }
        doneButton.setOnClickListener { finishReading() }

        // Back button does nothing until the reading is done.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (prefs.owedReadSeconds <= 0) finishReading()
            }
        })
    }

    private fun pickPassage() {
        val liked = Books.byId(prefs.likedBookId)
        if (liked != null) {
            book = liked
            discovering = false
            passageIndex = prefs.passageIndex % liked.passages.size
        } else {
            book = Books.all.random()
            discovering = true
            passageIndex = 0
        }
    }

    private fun showPassage() {
        titleText.text = book.title
        authorText.text = "by ${book.author}  ·  part ${passageIndex + 1} of ${book.passages.size}"
        passageText.text = book.passages[passageIndex]
        likeButton.visibility = if (discovering) View.VISIBLE else View.GONE
        otherButton.visibility = if (discovering) View.VISIBLE else View.GONE
        updateTimer()
    }

    private fun updateTimer() {
        val owed = prefs.owedReadSeconds
        if (owed > 0) {
            timerText.text = "Keep reading: ${Config.formatSeconds(owed)}"
            doneButton.isEnabled = false
            openButton.isEnabled = false
        } else {
            timerText.text = "Nice — you're free to go."
            doneButton.isEnabled = true
            openButton.isEnabled = true
        }
    }

    private fun finishReading() {
        if (!discovering) prefs.passageIndex = passageIndex + 1
        prefs.addLog("Finished break reading ${book.title}, part ${passageIndex + 1}")
        finish()
    }

    override fun onResume() {
        super.onResume()
        isShowing = true
        updateTimer()
        handler.postDelayed(tick, 1000)
    }

    override fun onPause() {
        super.onPause()
        isShowing = false
        handler.removeCallbacks(tick)
        if (prefs.owedReadSeconds > 0 && !isFinishing) {
            prefs.addLog("Left break early with ${Config.formatSeconds(prefs.owedReadSeconds)} still owed (${book.title})")
        }
    }
}
