package com.maxrave.domain.data.player

/** A slot and its exact item instance, rather than an ID that may occur twice in a queue. */
data class RenditionSwitchToken(val index: Int, val item: GenericMediaItem, val revision: Long = 0) {
    fun matches(index: Int, item: GenericMediaItem?, revision: Long = this.revision): Boolean =
        this.index == index && this.item === item && this.revision == revision
}

/** Refuse a stale prepared source without reconstructing or randomizing queue order. */
class RenditionSwitchSnapshot(
    private val token: RenditionSwitchToken,
    private val queue: List<GenericMediaItem>,
    private val shuffle: List<Int>,
    private val shuffled: Boolean,
    private val repeat: Int,
) {
    fun matches(
        index: Int,
        item: GenericMediaItem?,
        revision: Long,
        queue: List<GenericMediaItem>,
        shuffle: List<Int>,
        shuffled: Boolean,
        repeat: Int,
    ): Boolean = token.matches(index, item, revision) && this.queue.size == queue.size &&
        queue.indices.all { queue[it] === this.queue[it] } && this.shuffle == shuffle &&
        this.shuffled == shuffled && this.repeat == repeat
}

enum class RenditionSwitchResult { Success, Unavailable, Unsupported, Stale, Busy, Failed }

/** A duplicate ID can occupy a different slot with another explicit source intent. */
fun canReuseRenditionSource(cached: GenericMediaItem, target: GenericMediaItem): Boolean =
    cached.mediaId == target.mediaId && cached.videoPlayback == target.videoPlayback

/** End positions are exclusive; unknown duration must not reset a playing track to zero. */
fun renditionPosition(positionMs: Long, targetDurationMs: Long?): Long =
    positionMs.coerceAtLeast(0).let { position ->
        if (targetDurationMs != null && targetDurationMs > 0) position.coerceAtMost(targetDurationMs - 1) else position
    }
