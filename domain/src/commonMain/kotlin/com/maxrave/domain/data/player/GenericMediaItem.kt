package com.maxrave.domain.data.player

/**
 * Generic media item wrapper (no Media3 dependencies)
 */
data class GenericMediaItem(
    val mediaId: String,
    val uri: String?,
    val metadata: GenericMediaMetadata,
    val customCacheKey: String? = null,
    /** Explicit intent for a confirmed linked rendition; null preserves the user's preference. */
    val videoPlayback: Boolean? = null,
) {
    companion object {
        val EMPTY =
            GenericMediaItem(
                mediaId = "",
                uri = null,
                metadata = GenericMediaMetadata.EMPTY,
            )
    }
}
