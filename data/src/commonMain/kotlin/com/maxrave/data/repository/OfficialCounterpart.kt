package com.maxrave.data.repository

import com.maxrave.kotlinytmusicscraper.models.SongItem

/** Inspect both sides of an explicit pair; radio rows/currentIndex are not identity evidence. */
internal fun List<SongItem>.officialCounterpart(requestedId: String, musicVideo: Boolean): SongItem? {
    for (row in this) {
        val other = row.counterpart ?: continue
        val source: SongItem
        val target: SongItem
        when (requestedId) {
            row.id -> { source = row; target = other }
            other.id -> { source = other; target = row }
            else -> continue
        }
        if (source.isLive || target.isLive || source.id == target.id) continue
        val sourceType = if (musicVideo) "MUSIC_VIDEO_TYPE_ATV" else "MUSIC_VIDEO_TYPE_OMV"
        val targetType = if (musicVideo) "MUSIC_VIDEO_TYPE_OMV" else "MUSIC_VIDEO_TYPE_ATV"
        if (source.musicVideoType == sourceType && target.musicVideoType == targetType) return target
    }
    return null
}
