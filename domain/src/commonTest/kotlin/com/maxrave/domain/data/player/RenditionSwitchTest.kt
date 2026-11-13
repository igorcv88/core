package com.maxrave.domain.data.player

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RenditionSwitchTest {
    private fun item(id: String) = GenericMediaItem(id, id, GenericMediaMetadata.EMPTY)

    @Test fun duplicateIdsRequireTheOriginalSlotAndItemIdentity() {
        val first = item("same")
        val duplicate = item("same")
        val token = RenditionSwitchToken(0, first)
        assertTrue(token.matches(0, first))
        assertFalse(token.matches(1, duplicate))
        assertFalse(token.matches(0, first.copy()))
    }

    @Test fun elapsedPositionClampsOnlyToKnownTargetDuration() {
        assertEquals(9000, renditionPosition(9000, null))
        assertEquals(9000, renditionPosition(9000, 0))
        assertEquals(4999, renditionPosition(9000, 5000))
        assertEquals(0, renditionPosition(-1, 5000))
    }

    @Test fun leavingAndReturningToTheSameSlotInvalidatesTheRequest() {
        val current = item("same")
        val token = RenditionSwitchToken(0, current, revision = 4)
        assertTrue(token.matches(0, current, 4))
        assertFalse(token.matches(0, current, 6))
    }

    @Test fun queueAndExactShuffleChangesDuringPreparationRefuseTheCommit() {
        val first = item("same")
        val second = item("same")
        val third = item("third")
        val queue = listOf(first, second, third)
        val guard = RenditionSwitchSnapshot(RenditionSwitchToken(1, second), queue, listOf(1, 2, 0), true, 2)
        assertTrue(guard.matches(1, second, 0, queue, listOf(1, 2, 0), true, 2))
        assertFalse(guard.matches(1, second, 0, listOf(third, second, first), listOf(1, 2, 0), true, 2))
        assertFalse(guard.matches(1, second, 0, queue, listOf(1, 0, 2), true, 2))
        assertFalse(guard.matches(1, second, 0, queue, listOf(1, 2, 0), true, 1))
        assertFalse(guard.matches(1, second, 0, queue, listOf(1, 2, 0), false, 2))
    }

    @Test fun duplicateIdPrecacheCannotOverrideExplicitRenditionIntent() {
        val preferred = item("same")
        val video = preferred.copy(videoPlayback = true)
        val audio = preferred.copy(videoPlayback = false)
        assertTrue(canReuseRenditionSource(video, video.copy()))
        assertFalse(canReuseRenditionSource(preferred, video))
        assertFalse(canReuseRenditionSource(audio, video))
        assertFalse(canReuseRenditionSource(video, audio))
    }
}
