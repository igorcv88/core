package com.maxrave.common

/** TV streams buffer in ExoPlayer RAM; explicit downloads keep their independent read/write path. */
data class PlaybackPlatformPolicy private constructor(
    private val television: Boolean,
    val streamingDiskWrites: Boolean,
    val canvasDiskWrites: Boolean,
    val canvasLimitMiB: Int,
) {
    val readDownloads: Boolean get() = true
    val thumbnailLimitMiB: Int get() = if (television) 64 else 512
    fun allowPip(requested: Boolean, api: Int, supported: Boolean): Boolean = requested && !television && api >= 26 && supported

    companion object {
        fun forDevice(isTelevision: Boolean) = PlaybackPlatformPolicy(
            television = isTelevision,
            streamingDiskWrites = !isTelevision,
            canvasDiskWrites = !isTelevision,
            canvasLimitMiB = if (isTelevision) 32 else -1,
        )
    }
}
