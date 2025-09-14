package jp.unaguna.classloader.sp

import org.junit.jupiter.api.Test
import java.net.URLClassLoader
import kotlin.test.assertEquals

class SpringPackageScannerTest {
    @Test
    fun testScanAll() {
        val cpJarUrl = SpringPackageScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringPackageScanner(classLoader)

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(1, result.size)
        assertEquals("com.example", result[0].packageName)
    }

    @Test
    fun testScanWithAllCounter() {
        val cpJarUrl = SpringPackageScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringPackageScanner(classLoader).apply {
            applyClassCounter(SpringClassAllCounterFactory())
        }

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(1, result.size)
        assertEquals("com.example", result[0].packageName)
        assertEquals(8, result[0].getClassCount(SpringClassCounterType.ALL))
    }

    @Test
    fun testScanWithConcreteCounter() {
        val cpJarUrl = SpringPackageScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringPackageScanner(classLoader).apply {
            applyClassCounter(SpringConcreteClassCounterFactory())
        }

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(1, result.size)
        assertEquals("com.example", result[0].packageName)
        assertEquals(3, result[0].getClassCount(SpringClassCounterType.CONCRETE))
    }
}
