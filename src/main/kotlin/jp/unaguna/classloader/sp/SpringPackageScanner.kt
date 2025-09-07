package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClassCounter
import jp.unaguna.classloader.core.ClassCounterFactory
import jp.unaguna.classloader.core.PackageScanner
import jp.unaguna.classloader.core.ScannedPackageElement

class SpringPackageScanner(
    classLoader: ClassLoader,
) : PackageScanner<String, ClassFileMetadata, SpringPackageScannerElement, SpringClasspathScannerElement> {
    private val classCounterFactories: MutableSet<
        ClassCounterFactory<ClassFileMetadata, SpringClasspathScannerElement>
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

    override fun applyClassCounter(counter: ClassCounterFactory<ClassFileMetadata, SpringClasspathScannerElement>) {
        this.classCounterFactories.add(counter)
    }
}

class SpringPackageScannerElement(
    override val element: String,
) : ScannedPackageElement<String> {
    private val classCounter: MutableList<ClassCounter<ClassFileMetadata, SpringClasspathScannerElement>> =
        mutableListOf()

    override val packageName: String
        get() = element

    fun applyCounter(counter: ClassCounter<ClassFileMetadata, SpringClasspathScannerElement>) {
        classCounter.add(counter)
    }

    internal fun getClassCounters(): List<ClassCounter<ClassFileMetadata, SpringClasspathScannerElement>> {
        return classCounter
    }
}
