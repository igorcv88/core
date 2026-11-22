package com.maxrave.kotlinytmusicscraper.extractor

import com.maxrave.kotlinytmusicscraper.models.response.PlayerResponse

/**
 * Candidate selection for the conservative WEB_EMBEDDED fallback.
 *
 * An embedded response's signatureCipher is not a playable URL; only explicit HTTPS URLs are
 * considered. The caller validates each candidate with a network request before playback.
 */
internal fun PlayerResponse.StreamingData.embeddedDirectCandidates(
    audio: Boolean,
): List<PlayerResponse.StreamingData.Format> =
    (formats.orEmpty() + adaptiveFormats)
        .filter { it.isAudio == audio && it.url?.startsWith("https://") == true }
        .distinctBy { it.itag }
