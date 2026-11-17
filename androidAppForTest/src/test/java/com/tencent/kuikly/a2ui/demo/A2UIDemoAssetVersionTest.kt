package com.tencent.kuikly.a2ui.demo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class A2UIDemoAssetVersionTest {
    @Test
    fun versionAliasesStayWithinTheirOwnDirectory() {
        A2UIDemoAssetVersion.validate(listOf("v0.9", "v0.9.1"), "v0_9/a2ui_examples")
        A2UIDemoAssetVersion.validate(listOf("1.0", "v1.0"), "v1_0/a2ui_v10_tests")
    }

    @Test
    fun missingVersionDoesNotInheritThePreviousEnvelope() {
        assertThrows(IllegalArgumentException::class.java) {
            A2UIDemoAssetVersion.validate(listOf("1.0", null), "v1_0/a2ui_v10_tests")
        }
        assertThrows(IllegalArgumentException::class.java) {
            A2UIDemoAssetVersion.validate(emptyList(), "v0_9/a2ui_examples")
        }
    }

    @Test
    fun bothDirectoriesRejectTheOtherProtocol() {
        assertThrows(IllegalArgumentException::class.java) {
            A2UIDemoAssetVersion.validate(listOf("v0.9", "1.0"), "v0_9/a2ui_examples")
        }
        assertThrows(IllegalArgumentException::class.java) {
            A2UIDemoAssetVersion.validate(listOf("1.0", "v0.9"), "v1_0/a2ui_v10_tests")
        }
    }

    @Test
    fun configurationDoesNotChangeUnversionedCallers() {
        assertEquals("{\"debug\":true}", A2UIDemoAssetVersion.config("a2ui_components"))
        assertEquals("{\"debug\":true,\"protocolVersion\":\"1.0\",\"strictV10\":true}", A2UIDemoAssetVersion.config("v1_0/a2ui_v10_tests"))
        assertEquals("{\"debug\":true,\"protocolVersion\":\"0.9\",\"strictV10\":false}", A2UIDemoAssetVersion.config("v0_9/a2ui_examples"))
    }
}
