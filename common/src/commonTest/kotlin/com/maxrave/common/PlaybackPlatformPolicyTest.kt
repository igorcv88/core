package com.maxrave.common

import kotlin.test.*

class PlaybackPlatformPolicyTest {
    @Test fun televisionUsesMemoryForStreamingButKeepsDownloadsReadable() {
        val policy = PlaybackPlatformPolicy.forDevice(isTelevision = true)
        assertFalse(policy.streamingDiskWrites)
        assertFalse(policy.canvasDiskWrites)
        assertTrue(policy.readDownloads)
        assertEquals(32, policy.canvasLimitMiB)
        assertEquals(64, policy.thumbnailLimitMiB)
    }
    @Test fun mobileKeepsItsExistingCacheConfiguration() {
        val policy = PlaybackPlatformPolicy.forDevice(isTelevision = false)
        assertTrue(policy.streamingDiskWrites)
        assertTrue(policy.canvasDiskWrites)
        assertEquals(-1, policy.canvasLimitMiB)
        assertEquals(512, policy.thumbnailLimitMiB)
    }
    @Test fun televisionApi30NeverEntersPipEvenIfItsFeatureIsAdvertised() {
        val policy = PlaybackPlatformPolicy.forDevice(true)
        assertFalse(policy.allowPip(requested = true, api = 30, supported = false))
        assertFalse(policy.allowPip(requested = true, api = 30, supported = true))
    }
    @Test fun mobilePipRequiresRequestApiAndCapability() {
        val policy = PlaybackPlatformPolicy.forDevice(false)
        assertTrue(policy.allowPip(true, 30, true))
        assertFalse(policy.allowPip(true, 25, true))
        assertFalse(policy.allowPip(true, 30, false))
        assertFalse(policy.allowPip(false, 34, true))
    }
}
