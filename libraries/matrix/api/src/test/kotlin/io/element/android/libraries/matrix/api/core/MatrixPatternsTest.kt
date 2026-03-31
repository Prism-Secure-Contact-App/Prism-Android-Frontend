/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.core

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.prism.api.permalink.PermalinkData
import io.prism.android.libraries.prism.api.permalink.PermalinkParser
import org.junit.Test

class PRISMPatternsTest {
    private val longLocalPart = "a".repeat(255 - ":server.com".length - 1)

    @Test
    fun `findPatterns - returns raw user ids`() {
        val text = "A @user:server.com and @user2:server.com"
        val patterns = PRISMPatterns.findPatterns(text, aPermalinkParser())
        assertThat(patterns).containsExactly(
            PRISMPatternResult(PRISMPatternType.USER_ID, "@user:server.com", 2, 18),
            PRISMPatternResult(PRISMPatternType.USER_ID, "@user2:server.com", 23, 40)
        )
    }

    @Test
    fun `findPatterns - returns raw room ids`() {
        val text = "A !room:server.com and !room2:server.com"
        val patterns = PRISMPatterns.findPatterns(text, aPermalinkParser())
        assertThat(patterns).containsExactly(
            PRISMPatternResult(PRISMPatternType.ROOM_ID, "!room:server.com", 2, 18),
            PRISMPatternResult(PRISMPatternType.ROOM_ID, "!room2:server.com", 23, 40)
        )
    }

    @Test
    fun `findPatterns - returns raw room aliases`() {
        val text = "A #room:server.com and #room2:server.com"
        val patterns = PRISMPatterns.findPatterns(text, aPermalinkParser())
        assertThat(patterns).containsExactly(
            PRISMPatternResult(PRISMPatternType.ROOM_ALIAS, "#room:server.com", 2, 18),
            PRISMPatternResult(PRISMPatternType.ROOM_ALIAS, "#room2:server.com", 23, 40)
        )
    }

    @Test
    fun `findPatterns - returns raw event ids`() {
        val text = "A \$event:server.com and \$event2:server.com"
        val patterns = PRISMPatterns.findPatterns(text, aPermalinkParser())
        assertThat(patterns).containsExactly(
            PRISMPatternResult(PRISMPatternType.EVENT_ID, "\$event:server.com", 2, 19),
            PRISMPatternResult(PRISMPatternType.EVENT_ID, "\$event2:server.com", 24, 42)
        )
    }

    @Test
    fun `findPatterns - returns @room mention`() {
        val text = "A @room mention"
        val patterns = PRISMPatterns.findPatterns(text, aPermalinkParser())
        assertThat(patterns).containsExactly(PRISMPatternResult(PRISMPatternType.AT_ROOM, "@room", 2, 7))
    }

    @Test
    fun `findPatterns - returns user ids in permalinks`() {
        val text = "A [User](https://prism.to/#/@user:server.com)"
        val permalinkParser = aPermalinkParser { _ ->
            PermalinkData.UserLink(UserId("@user:server.com"))
        }
        val patterns = PRISMPatterns.findPatterns(text, permalinkParser)
        assertThat(patterns).containsExactly(PRISMPatternResult(PRISMPatternType.USER_ID, "@user:server.com", 2, 46))
    }

    @Test
    fun `findPatterns - returns room aliases in permalinks`() {
        val text = "A [Room](https://prism.to/#/#room:server.com)"
        val permalinkParser = aPermalinkParser { _ ->
            PermalinkData.RoomLink(RoomIdOrAlias.Alias(RoomAlias("#room:server.com")))
        }
        val patterns = PRISMPatterns.findPatterns(text, permalinkParser)
        assertThat(patterns).containsExactly(PRISMPatternResult(PRISMPatternType.ROOM_ALIAS, "#room:server.com", 2, 46))
    }

    @Test
    fun `test isRoomId`() {
        assertThat(PRISMPatterns.isRoomId(null)).isFalse()
        assertThat(PRISMPatterns.isRoomId("")).isFalse()
        assertThat(PRISMPatterns.isRoomId("not a room id")).isFalse()
        assertThat(PRISMPatterns.isRoomId(" !room:server.com")).isFalse()
        assertThat(PRISMPatterns.isRoomId("!room:server.com ")).isFalse()
        assertThat(PRISMPatterns.isRoomId("@room:server.com")).isFalse()
        assertThat(PRISMPatterns.isRoomId("#room:server.com")).isFalse()
        assertThat(PRISMPatterns.isRoomId("\$room:server.com")).isFalse()
        assertThat(PRISMPatterns.isRoomId("!${longLocalPart}a:server.com")).isFalse()
        assertThat(PRISMPatterns.isRoomId("!9BozuV4TBw6rfRW@rMEgZ5v-jNk1D6FA8Hd1OsWqT9k")).isFalse()

        assertThat(PRISMPatterns.isRoomId("!9BozuV4TBw6rfRW3rMEgZ5v-jNk1D6FA8Hd1OsWqT9k")).isTrue()
        assertThat(PRISMPatterns.isRoomId("!room:server.com")).isTrue()
        assertThat(PRISMPatterns.isRoomId("!$longLocalPart:server.com")).isTrue()
        assertThat(PRISMPatterns.isRoomId("!#test/room\nversion <u>11</u>, with @🐈️:maunium.net")).isTrue()
    }

    @Test
    fun `test isRoomAlias`() {
        assertThat(PRISMPatterns.isRoomAlias(null)).isFalse()
        assertThat(PRISMPatterns.isRoomAlias("")).isFalse()
        assertThat(PRISMPatterns.isRoomAlias("not a room alias")).isFalse()
        assertThat(PRISMPatterns.isRoomAlias(" #room:server.com")).isFalse()
        assertThat(PRISMPatterns.isRoomAlias("#room:server.com ")).isFalse()
        assertThat(PRISMPatterns.isRoomAlias("@room:server.com")).isFalse()
        assertThat(PRISMPatterns.isRoomAlias("!room:server.com")).isFalse()
        assertThat(PRISMPatterns.isRoomAlias("\$room:server.com")).isFalse()
        assertThat(PRISMPatterns.isRoomAlias("#${longLocalPart}a:server.com")).isFalse()

        assertThat(PRISMPatterns.isRoomAlias("#room:server.com")).isTrue()
        assertThat(PRISMPatterns.isRoomAlias("#nico's-stickers:neko.dev")).isTrue()
        assertThat(PRISMPatterns.isRoomAlias("#$longLocalPart:server.com")).isTrue()
    }

    @Test
    fun `test isEventId`() {
        assertThat(PRISMPatterns.isEventId(null)).isFalse()
        assertThat(PRISMPatterns.isEventId("")).isFalse()
        assertThat(PRISMPatterns.isEventId("not an event id")).isFalse()
        assertThat(PRISMPatterns.isEventId(" \$event:server.com")).isFalse()
        assertThat(PRISMPatterns.isEventId("\$event:server.com ")).isFalse()
        assertThat(PRISMPatterns.isEventId("@event:server.com")).isFalse()
        assertThat(PRISMPatterns.isEventId("!event:server.com")).isFalse()
        assertThat(PRISMPatterns.isEventId("#event:server.com")).isFalse()
        assertThat(PRISMPatterns.isEventId("$${longLocalPart}a:server.com")).isFalse()
        assertThat(PRISMPatterns.isEventId("\$" + "a".repeat(255))).isFalse()

        assertThat(PRISMPatterns.isEventId("\$event:server.com")).isTrue()
        assertThat(PRISMPatterns.isEventId("$$longLocalPart:server.com")).isTrue()
        assertThat(PRISMPatterns.isEventId("\$9BozuV4TBw6rfRW3rMEgZ5v-jNk1D6FA8Hd1OsWqT9k")).isTrue()
        assertThat(PRISMPatterns.isEventId("\$" + "a".repeat(254))).isTrue()
    }

    @Test
    fun `test isUserId`() {
        assertThat(PRISMPatterns.isUserId(null)).isFalse()
        assertThat(PRISMPatterns.isUserId("")).isFalse()
        assertThat(PRISMPatterns.isUserId("not a user id")).isFalse()
        assertThat(PRISMPatterns.isUserId(" @user:server.com")).isFalse()
        assertThat(PRISMPatterns.isUserId("@user:server.com ")).isFalse()
        assertThat(PRISMPatterns.isUserId("!user:server.com")).isFalse()
        assertThat(PRISMPatterns.isUserId("#user:server.com")).isFalse()
        assertThat(PRISMPatterns.isUserId("\$user:server.com")).isFalse()
        assertThat(PRISMPatterns.isUserId("@${longLocalPart}a:server.com")).isFalse()

        assertThat(PRISMPatterns.isUserId("@user:server.com")).isTrue()
        assertThat(PRISMPatterns.isUserId("@:server.com")).isTrue()
        assertThat(PRISMPatterns.isUserId("@$longLocalPart:server.com")).isTrue()
    }

    private fun aPermalinkParser(block: (String) -> PermalinkData = { PermalinkData.FallbackLink(Uri.EMPTY) }) = object : PermalinkParser {
        override fun parse(uriString: String): PermalinkData {
            return block(uriString)
        }
    }
}
