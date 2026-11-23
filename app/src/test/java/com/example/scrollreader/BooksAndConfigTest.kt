package com.example.scrollreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Checks the hard-coded book data and timer helpers that the reading screen depends on. */
class BooksAndConfigTest {

    @Test
    fun everyBookHasTitleAuthorLinkAndAtLeastOneExcerpt() {
        assertTrue("need at least two books for 'Show me a different book'", Books.all.size >= 2)
        for (book in Books.all) {
            assertTrue("${book.id} title", book.title.isNotBlank())
            assertTrue("${book.id} author", book.author.isNotBlank())
            assertTrue("${book.id} link", book.fullBookUrl.startsWith("https://"))
            assertTrue("${book.id} passages", book.passages.isNotEmpty())
            book.passages.forEach { assertTrue("${book.id} empty passage", it.isNotBlank()) }
        }
    }

    @Test
    fun bookIdsAreUnique() {
        val ids = Books.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun byIdFindsBooksAndReturnsNullForUnknownIds() {
        assertEquals("Moby-Dick", Books.byId("moby")?.title)
        assertNull(Books.byId("not-a-book"))
        assertNull(Books.byId(null))
    }

    @Test
    fun formatSecondsShowsMinutesAndSeconds() {
        assertEquals("0:00", Config.formatSeconds(0))
        assertEquals("0:20", Config.formatSeconds(20))
        assertEquals("1:05", Config.formatSeconds(65))
        assertEquals("10:00", Config.formatSeconds(600))
    }

    @Test
    fun testSessionLengthIsShortAndPositive() {
        assertTrue(Config.TEST_READ_SECONDS in 1..Config.MAX_READ_SECONDS)
    }
}
