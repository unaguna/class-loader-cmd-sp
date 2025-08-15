package jp.unaguna.classloader.sp

import org.junit.jupiter.api.Test
import java.net.URLClassLoader
import kotlin.test.assertEquals

class SpringClasspathScannerTest {
    @Test
    fun testScanAll() {
        val cpJarUrl = SpringClasspathScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringClasspathScanner(classLoader)

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(8, result.size)
    }

    @Test
    fun testScanByBaseClass() {
        val cpJarUrl = SpringClasspathScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringClasspathScanner(classLoader).apply {
            subtypeOf(classLoader.loadClass("com.example.BaseClass"))
        }

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(2, result.size)
        assertEquals(
            setOf("com.example.AbsSubClass", "com.example.BaseClass"),
            result.map { it.className }.toSet(),
        )
    }

    @Test
    fun testScanByBaseInterface() {
        val cpJarUrl = SpringClasspathScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringClasspathScanner(classLoader).apply {
            subtypeOf(classLoader.loadClass("com.example.Interface"))
        }

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(3, result.size)
        assertEquals(
            setOf("com.example.Interface", "com.example.SubInterface", "com.example.ImplClass"),
            result.map { it.className }.toSet(),
        )
    }
}
