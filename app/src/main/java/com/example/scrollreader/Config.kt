package com.example.scrollreader

/**
 * Everything the group is likely to tweak lives here.
 */
object Config {
    // How long someone can scroll before a reading break. Short default so testing is quick;
    // you can change it in the app's main screen without editing code.
    const val DEFAULT_SCROLL_LIMIT_SECONDS = 60

    // "1:2 scrolling:reading" = 2 seconds of reading owed for every 1 second of scrolling.
    // Change in the app's main screen (e.g. 0.5 means 1 minute of reading per 2 minutes of scrolling).
    const val DEFAULT_READ_RATIO = 2.0f

    // Never ask for more than this much reading in one break.
    const val MAX_READ_SECONDS = 600

    // Length of the "Test interruption now" button's break.
    const val TEST_READ_SECONDS = 20

    // Apps that count as "scrolling". Package name -> friendly name for the log.
    val TARGET_APPS = mapOf(
        "com.zhiliaoapp.musically" to "TikTok",
        "com.ss.android.ugc.trill" to "TikTok",
        "com.instagram.android" to "Instagram",
        "com.google.android.youtube" to "YouTube",
        "com.twitter.android" to "X",
        "com.reddit.frontpage" to "Reddit",
        "com.facebook.katana" to "Facebook",
        "com.snapchat.android" to "Snapchat",
        "com.pinterest" to "Pinterest",
    )

    // Windows that pop up on top of apps (notification shade, keyboards) and shouldn't
    // count as "leaving" the app you were scrolling.
    fun isIgnored(pkg: String): Boolean =
        pkg == "com.android.systemui" ||
            pkg.contains("inputmethod") ||
            pkg.contains("keyboard") ||
            pkg.contains("honeyboard")

    fun formatSeconds(s: Int): String = "%d:%02d".format(s / 60, s % 60)
}
