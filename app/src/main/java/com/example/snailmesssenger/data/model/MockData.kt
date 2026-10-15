package com.example.snailmesssenger.data.model

import com.example.snailmesssenger.data.model.Message.Companion.ME_ID

/**
 * Local mock data for prototype development.
 * These conversations are NOT real — they exist only to exercise the UI
 * during Phase 1. A real messaging backend will replace this source later.
 */
object MockData {

    val demoUser = User(
        id = ME_ID,
        username = "you",
        displayName = "You",
        bio = "",
        isOnline = true,
    )

    private val contacts = listOf(
        User(id = "u1", username = "ara", displayName = "Ara Okafor", bio = "Designing calm interfaces.", isOnline = true),
        User(id = "u2", username = "ben", displayName = "Ben Nakamura", bio = "Coffee, code, quiet.", isOnline = false),
        User(id = "u3", username = "chi", displayName = "Chi Rossi", bio = "Privacy enthusiast.", isOnline = false),
        User(id = "u4", username = "dara", displayName = "Dara Silva", bio = "Reads a lot of books.", isOnline = true),
    )

    private fun ts(hoursAgo: Int): Long =
        System.currentTimeMillis() - hoursAgo * 3_600_000L

    val conversations: List<Conversation> = listOf(
        Conversation(
            id = "c1",
            peer = contacts[0],
            unreadCount = 2,
            messages = listOf(
                Message("m1", "u1", "Hey, did you get a chance to look at the new mockups?", ts(2)),
                Message("m2", ME_ID, "Yes — the dark theme feels really balanced.", ts(2)),
                Message("m3", "u1", "Agreed. Want to test the animation toggle together?", ts(1)),
            ),
        ),
        Conversation(
            id = "c2",
            peer = contacts[1],
            unreadCount = 0,
            messages = listOf(
                Message("m4", ME_ID, "Sending over the build later today.", ts(5)),
                Message("m5", "u2", "Perfect, no rush. Enjoy your coffee.", ts(4)),
            ),
        ),
        Conversation(
            id = "c3",
            peer = contacts[2],
            unreadCount = 0,
            messages = listOf(
                Message("m6", "u3", "The local-only approach is refreshing for a prototype.", ts(26)),
            ),
        ),
        Conversation(
            id = "c4",
            peer = contacts[3],
            unreadCount = 0,
            messages = listOf(
                Message("m7", ME_ID, "That article you shared was excellent.", ts(49)),
                Message("m8", "u4", "Right? I will send the follow-up tomorrow.", ts(48)),
            ),
        ),
    )
}
