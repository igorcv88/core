package com.maxrave.kotlinytmusicscraper.extractor

import com.maxrave.kotlinytmusicscraper.models.response.PlayerResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmbeddedStreamFallbackTest {
    private fun format(
        itag: Int,
        url: String?,
        width: Int? = null,
        cipher: String? = null,
    ) = PlayerResponse.StreamingData.Format(
        itag = itag,
        url = url,
        mimeType = if (width == null) "audio/webm" else "video/mp4",
        bitrate = 128000,
        width = width,
        height = if (width == null) null else 360,
        contentLength = null,
        quality = "medium",
        fps = null,
        qualityLabel = null,
        averageBitrate = null,
        audioQuality = null,
        approxDurationMs = null,
        audioSampleRate = null,
        audioChannels = null,
        loudnessDb = null,
        lastModified = null,
        signatureCipher = cipher,
    )

    @Test
    fun onlyDirectHttpsAudioIsEligible() {
        val audio = format(251, "https://rr1.googlevideo.com/videoplayback")
        val signedButUnresolved = format(140, null, cipher = "s=secret&url=https%3A%2F%2Fexample.com")
        val insecure = format(250, "http://rr1.googlevideo.com/videoplayback")
        val video = format(18, "https://rr1.googlevideo.com/videoplayback", width = 640)
        val data = PlayerResponse.StreamingData(
            hlsManifestUrl = null,
            formats = listOf(signedButUnresolved, insecure, audio),
            adaptiveFormats = listOf(audio, video),
            expiresInSeconds = 100,
        )
        assertEquals(listOf(251), data.embeddedDirectCandidates(audio = true).map { it.itag })
        assertEquals(listOf(18), data.embeddedDirectCandidates(audio = false).map { it.itag })
    }

    @Test
    fun cipherOnlyFormatsCannotMasqueradeAsPlayableStreams() {
        val data = PlayerResponse.StreamingData(
            hlsManifestUrl = null,
            formats = null,
            adaptiveFormats = listOf(format(251, null, cipher = "s=abc")),
            expiresInSeconds = 100,
        )
        assertTrue(data.embeddedDirectCandidates(audio = true).isEmpty())
    }
}
