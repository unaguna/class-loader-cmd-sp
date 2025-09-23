package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClassReducer
import jp.unaguna.classloader.core.ClassReducerFactory
import jp.unaguna.classloader.core.ClassReducerType
import jp.unaguna.classloader.core.PackageScanner
import jp.unaguna.classloader.core.ScannedPackageElement

class SpringPackageScanner(
    classLoader: ClassLoader,
) : PackageScanner<
    String,
    ClassFileMetadata,
    SpringPackageScannerElement,
    SpringClasspathScannerElement,
    > {
    private val classReducerFactories: MutableSet<
        ClassReducerFactory<ClassFileMetadata, SpringClasspathScannerElement, *>
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
            for (reducer in scanned.getClassReducers()) {
                reducer.apply(scannedClass)
            }
        }

        return packages.values.iterator()
    }

    private fun createScannedInstance(packageName: String): SpringPackageScannerElement {
        return SpringPackageScannerElement(packageName).apply {
            for (classReducerFactory in classReducerFactories) {
                applyReducer(classReducerFactory.create())
            }
        }
    }

    override fun applyClassReducer(
        reducerFactory: ClassReducerFactory<ClassFileMetadata, SpringClasspathScannerElement, *>,
    ) {
        this.classReducerFactories.add(reducerFactory)
    }

    fun applyClassReducer(reducerType: SpringClassReducerType<*>) {
        this.applyClassReducer(reducerType.createFactory())
    }

    override fun pattern(packageNamePatterns: Iterable<String>) {
        classpathScanner.pattern(packageNamePatterns.map { "$it.*" })
    }
}

class SpringPackageScannerElement(
    override val element: String,
) : ScannedPackageElement<String> {
    private val classReducer: MutableMap<
        ClassReducerType<*>,
        ClassReducer<ClassFileMetadata, SpringClasspathScannerElement, *>
        > = mutableMapOf()

    override val packageName: String
        get() = element

    override fun <R> getClassReducedValue(condition: ClassReducerType<R>): R {
        val counter = this.classReducer[condition]
            ?: throw IllegalArgumentException(condition.toString())

        return counter.value as? R
            ?: throw ClassCastException()
    }

    fun applyReducer(
        reducer: ClassReducer<ClassFileMetadata, SpringClasspathScannerElement, *>,
    ) {
        classReducer[reducer.type] = reducer
    }

    internal fun getClassReducers(): List<ClassReducer<ClassFileMetadata, SpringClasspathScannerElement, *>> {
        return classReducer.values.toList()
    }
}
