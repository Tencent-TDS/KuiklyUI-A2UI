package com.tencent.kuikly.a2ui.demo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class A2UIDemoAssetPathTest {
    @Test
    fun emptySubdirectoryDoesNotAppendASlash() {
        assertEquals("v0_9/a2ui_components/text", A2UIDemoAssetPath.join("v0_9/a2ui_components/text", ""))
        assertEquals("v1_0/a2ui_components/text", A2UIDemoAssetPath.join("v1_0/a2ui_components/text", ""))
    }

    @Test
    fun directAndExistingDirectoryEntriesResolveToTheSamePath() {
        assertEquals(A2UIDemoAssetPath.join("v0_9/a2ui_components", "text"),
            A2UIDemoAssetPath.join("v0_9/a2ui_components/text", ""))
    }

    @Test
    fun filePathHasNoEmptySegments() {
        assertEquals("v1_0/a2ui_components/text/4_text_body.json",
            A2UIDemoAssetPath.join("v1_0/a2ui_components/text/", "", "4_text_body.json"))
    }

    @Test
    fun redundantSlashesAreNormalized() {
        assertEquals("v0_9/a2ui_components/text", A2UIDemoAssetPath.join("/v0_9//a2ui_components/", "/text/"))
    }

    @Test
    fun emptyPathsAndTraversalAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { A2UIDemoAssetPath.join("", "/") }
        assertThrows(IllegalArgumentException::class.java) { A2UIDemoAssetPath.join("v0_9", "../text") }
        assertThrows(IllegalArgumentException::class.java) { A2UIDemoAssetPath.join("v1_0", "./text") }
    }
}
