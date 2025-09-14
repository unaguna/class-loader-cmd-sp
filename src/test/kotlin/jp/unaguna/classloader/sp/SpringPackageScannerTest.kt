package jp.unaguna.classloader.sp

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
    fun testScanByPackageName() {
        val cpJarUrl = SpringPackageScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringPackageScanner(classLoader).apply {
            pattern(listOf("com.**"))
        }

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
            applyClassReducer(SpringClassAllCounterFactory())
        }

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(1, result.size)
        assertEquals("com.example", result[0].packageName)
        assertEquals(8, result[0].getClassReducedValue(SpringClassReducerType.ALL))
    }

    @Test
    fun testScanWithConcreteCounter() {
        val cpJarUrl = SpringPackageScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringPackageScanner(classLoader).apply {
            applyClassReducer(SpringConcreteClassCounterFactory())
        }

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(1, result.size)
        assertEquals("com.example", result[0].packageName)
        assertEquals(3, result[0].getClassReducedValue(SpringClassReducerType.CONCRETE))
    }

    @Test
    fun testScanWith2Counters() {
        val cpJarUrl = SpringPackageScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringPackageScanner(classLoader).apply {
            applyClassReducer(SpringClassAllCounterFactory())
            applyClassReducer(SpringConcreteClassCounterFactory())
        }

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(1, result.size)
        assertEquals("com.example", result[0].packageName)
        assertEquals(8, result[0].getClassReducedValue(SpringClassReducerType.ALL))
        assertEquals(3, result[0].getClassReducedValue(SpringClassReducerType.CONCRETE))
    }

    @Test
    fun testScan__error_with_unapplied_counter() {
        val cpJarUrl = SpringPackageScannerTest::class.java.getClassLoader().getResource("jar_for_test/sample.jar")
        val classLoader = URLClassLoader(arrayOf(cpJarUrl), null)
        val scanner = SpringPackageScanner(classLoader).apply {
            applyClassReducer(SpringConcreteClassCounterFactory())
        }

        val result = scanner.scan().asSequence().toList()

        println(result)

        assertEquals(1, result.size)
        val resultOne = result[0]
        val actualExc = assertThrows<IllegalArgumentException> {
            resultOne.getClassReducedValue(SpringClassReducerType.ALL)
        }
        assertEquals(actualExc.message, SpringClassReducerType.ALL.toString())
    }
}
