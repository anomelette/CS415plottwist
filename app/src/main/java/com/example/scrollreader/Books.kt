package com.example.scrollreader

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val fullBookUrl: String,
    val passages: List<String>,
)

/**
 * Hard-coded public-domain excerpts (Project Gutenberg) so the prototype needs no server.
 * To add a book: copy one entry, give it a new id, paste passages in reading order.
 */
object Books {
    val all = listOf(
        Book(
            id = "pride",
            title = "Pride and Prejudice",
            author = "Jane Austen",
            fullBookUrl = "https://www.gutenberg.org/ebooks/1342",
            passages = listOf(
                "It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife.\n\n" +
                    "However little known the feelings or views of such a man may be on his first entering a neighbourhood, this truth is so well fixed in the minds of the surrounding families, that he is considered the rightful property of some one or other of their daughters.\n\n" +
                    "“My dear Mr. Bennet,” said his lady to him one day, “have you heard that Netherfield Park is let at last?”\n\n" +
                    "Mr. Bennet replied that he had not.\n\n" +
                    "“But it is,” returned she; “for Mrs. Long has just been here, and she told me all about it.”\n\n" +
                    "Mr. Bennet made no answer.\n\n" +
                    "“Do you not want to know who has taken it?” cried his wife impatiently.\n\n" +
                    "“You want to tell me, and I have no objection to hearing it.”\n\n" +
                    "This was invitation enough.",
                "“Why, my dear, you must know, Mrs. Long says that Netherfield is taken by a young man of large fortune from the north of England; that he came down on Monday in a chaise and four to see the place, and was so much delighted with it, that he agreed with Mr. Morris immediately; that he is to take possession before Michaelmas, and some of his servants are to be in the house by the end of next week.”\n\n" +
                    "“What is his name?”\n\n" +
                    "“Bingley.”\n\n" +
                    "“Is he married or single?”\n\n" +
                    "“Oh! Single, my dear, to be sure! A single man of large fortune; four or five thousand a year. What a fine thing for our girls!”\n\n" +
                    "“How so? How can it affect them?”",
            ),
        ),
        Book(
            id = "alice",
            title = "Alice's Adventures in Wonderland",
            author = "Lewis Carroll",
            fullBookUrl = "https://www.gutenberg.org/ebooks/11",
            passages = listOf(
                "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, “and what is the use of a book,” thought Alice “without pictures or conversations?”\n\n" +
                    "So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her.",
                "There was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself, “Oh dear! Oh dear! I shall be late!” (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across the field after it, and fortunately was just in time to see it pop down a large rabbit-hole under the hedge.",
            ),
        ),
        Book(
            id = "moby",
            title = "Moby-Dick",
            author = "Herman Melville",
            fullBookUrl = "https://www.gutenberg.org/ebooks/2701",
            passages = listOf(
                "Call me Ishmael. Some years ago—never mind how long precisely—having little or no money in my purse, and nothing particular to interest me on shore, I thought I would sail about a little and see the watery part of the world. It is a way I have of driving off the spleen and regulating the circulation.\n\n" +
                    "Whenever I find myself growing grim about the mouth; whenever it is a damp, drizzly November in my soul; whenever I find myself involuntarily pausing before coffin warehouses, and bringing up the rear of every funeral I meet; and especially whenever my hypos get such an upper hand of me, that it requires a strong moral principle to prevent me from deliberately stepping into the street, and methodically knocking people's hats off—then, I account it high time to get to sea as soon as I can.",
                "There now is your insular city of the Manhattoes, belted round by wharves as Indian isles by coral reefs—commerce surrounds it with her surf. Right and left, the streets take you waterward. Its extreme downtown is the battery, where that noble mole is washed by waves, and cooled by breezes, which a few hours previous were out of sight of land. Look at the crowds of water-gazers there.",
            ),
        ),
        Book(
            id = "holmes",
            title = "The Adventures of Sherlock Holmes",
            author = "Arthur Conan Doyle",
            fullBookUrl = "https://www.gutenberg.org/ebooks/1661",
            passages = listOf(
                "To Sherlock Holmes she is always the woman. I have seldom heard him mention her under any other name. In his eyes she eclipses and predominates the whole of her sex. It was not that he felt any emotion akin to love for Irene Adler. All emotions, and that one particularly, were abhorrent to his cold, precise but admirably balanced mind. He was, I take it, the most perfect reasoning and observing machine that the world has seen, but as a lover he would have placed himself in a false position.",
                "He never spoke of the softer passions, save with a gibe and a sneer. They were admirable things for the observer—excellent for drawing the veil from men's motives and actions.",
            ),
        ),
    )

    fun byId(id: String?): Book? = all.firstOrNull { it.id == id }
}
