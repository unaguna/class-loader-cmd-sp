package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClassCounter
import jp.unaguna.classloader.core.ClassCounterFactory
import jp.unaguna.classloader.core.PackageScanner
import jp.unaguna.classloader.core.ScannedPackageElement

class SpringPackageScanner(
    classLoader: ClassLoader,
) : PackageScanner<
    String,
    ClassFileMetadata,
    SpringPackageScannerElement,
    SpringClasspathScannerElement,
    SpringClassCounterType
    > {
    private val classCounterFactories: MutableSet<
        ClassCounterFactory<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType>
        > = mutableSetOf()
    private val classpathScanner = SpringClasspathScanner(classLoader)

    override fun scan(): Iterator<SpringPackageScannerElement> {
        val packages: MutableMap<String, SpringPackageScannerElement> = mutableMapOf()

        for (scannedClass in classpathScanner.scan()) {
            val packageName = scannedClass.packageName ?: continue
            if (!packages.containsKey(packageName)) {
                packages[packageName] = createScannedInstance(packageName)
            }

            val scanned = packages[packageName]!!
            for (counter in scanned.getClassCounters()) {
                counter.countIfMatch(scannedClass)
            }
        }

        return packages.values.iterator()
    }

    private fun createScannedInstance(packageName: String): SpringPackageScannerElement {
        return SpringPackageScannerElement(packageName).apply {
            for (classCounterFactory in classCounterFactories) {
                applyCounter(classCounterFactory.create())
            }
        }
    }

    override fun applyClassCounter(
        counterFactory: ClassCounterFactory<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType>,
    ) {
        this.classCounterFactories.add(counterFactory)
    }

    override fun pattern(packageNamePatterns: Iterable<String>) {
        classpathScanner.pattern(packageNamePatterns.map { "$it.*" })
    }
}

class SpringPackageScannerElement(
    override val element: String,
) : ScannedPackageElement<String, SpringClassCounterType> {
    private val classCounter: MutableMap<
        SpringClassCounterType,
        ClassCounter<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType>
        > = mutableMapOf()

    override val packageName: String
        get() = element

    override fun getClassCount(condition: SpringClassCounterType): Int {
        val counter = this.classCounter[condition]
            ?: throw IllegalArgumentException(condition.toString())

        return counter.count
    }

    fun applyCounter(counter: ClassCounter<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType>) {
        classCounter[counter.type] = counter
    }

    internal fun getClassCounters():
        List<ClassCounter<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType>> {
        return classCounter.values.toList()
    }
}
