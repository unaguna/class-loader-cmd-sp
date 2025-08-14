package jp.unaguna.classloader.sp

import org.junit.jupiter.api.Test
import java.net.URLClassLoader
import kotlin.test.assertEquals

class SpringClasspathScannerTest {
    @Test
    fun testScanByBaseClass() {
        val cpJarUrl = SpringClasspathScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringClasspathScanner(classLoader).apply {
            subclassOf(classLoader.loadClass("java.lang.Object"))
        }

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(4, result.size)
    }
}
