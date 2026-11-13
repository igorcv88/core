package com.maxrave.data.repository

import com.maxrave.kotlinytmusicscraper.models.SongItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OfficialCounterpartTest {
    private fun song(id: String, type: String?, counterpart: SongItem? = null) =
        SongItem(id, "Same title", emptyList(), thumbnail = "", musicVideoType = type, counterpart = counterpart)

    @Test fun exactLinkWorksFromEitherSideOfTheRow() {
        val video = song("video", "MUSIC_VIDEO_TYPE_OMV")
        val audio = song("song", "MUSIC_VIDEO_TYPE_ATV", video)
        assertEquals("video", listOf(audio).officialCounterpart("song", true)?.id)
        assertEquals("song", listOf(audio).officialCounterpart("video", false)?.id)
    }

    @Test fun unrelatedFirstRowAndMatchingTitlesAreNeverUsed() {
        val unrelated = song("other", "MUSIC_VIDEO_TYPE_ATV", song("wrong", "MUSIC_VIDEO_TYPE_OMV"))
        val requested = song("wanted", "MUSIC_VIDEO_TYPE_ATV")
        assertNull(listOf(unrelated, requested).officialCounterpart("wanted", true))
        assertNull(listOf(unrelated).officialCounterpart("missing", true))
    }

    @Test fun unknownSameTypeLiveAndUnofficialVideoAreUnavailable() {
        for (type in listOf(null, "Song", "MUSIC_VIDEO_TYPE_ATV", "MUSIC_VIDEO_TYPE_UGC")) {
            assertNull(listOf(song("song", "MUSIC_VIDEO_TYPE_ATV", song("target", type))).officialCounterpart("song", true))
        }
        val live = song("video", "MUSIC_VIDEO_TYPE_OMV").copy(isLive = true)
        assertNull(listOf(song("song", "MUSIC_VIDEO_TYPE_ATV", live)).officialCounterpart("song", true))
    }
}
