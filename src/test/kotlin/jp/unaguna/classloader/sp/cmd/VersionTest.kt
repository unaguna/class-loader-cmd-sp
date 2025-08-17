package jp.unaguna.classloader.sp.cmd

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class VersionTest {
    @Test
    fun testVersion() {
        val version = Version().getVersion()
        assertNotNull(version)
    }
}
