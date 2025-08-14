package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClasspathScannerResettable
import jp.unaguna.classloader.core.ScannedElement
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.type.ClassMetadata
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.core.type.filter.TypeFilter
import java.net.URL

class SpringClasspathScanner(
    classLoader: ClassLoader,
): ClasspathScannerResettable<ClassFileMetadata, SpringClasspathScannerElement> {
    private var basePackage: String? = null
    private var classExtensionTree: Boolean = false
    private val includeFilters: MutableList<TypeFilter> = mutableListOf()

    private val resolver = PathMatchingResourcePatternResolver(classLoader)

    override fun scan(): Iterator<SpringClasspathScannerElement> {
        val packageSearchPath = "classpath*:" + (basePackage ?: "").replace('.', '/') + "/**/*.class"
        return SpringClasspathScannerIterator(
            resolver.getResources(packageSearchPath).toList(),
            classExtensionTree = this.classExtensionTree,
            resolver = this.resolver,
            includeFilters = this.includeFilters,
        )
    }
    private fun addIncludeFilter(typeFilter: TypeFilter) {
        includeFilters.add(typeFilter)
    }

    private fun resetIncludeFilter() {
        includeFilters.clear()
    }

    override fun subclassOf(cls: Class<*>) {
        this.addIncludeFilter(AssignableTypeFilter(cls))
    }

    override fun inPackage(basePackage: String) {
        this.basePackage = basePackage
    }

    override fun asClassExtensionTree() {
        this.classExtensionTree = true
    }

    override fun clearConditions() {
         this.resetIncludeFilter()
         basePackage = null
         classExtensionTree = false
    }
}

data class ClassFileMetadata(
    val resource: Resource,
    val metadataReader: MetadataReader,
    val classMetadata: ClassMetadata,
)

private class SpringClasspathScannerIterator(
    scanned: Iterable<Resource>,
    resolver: ResourcePatternResolver,
    classExtensionTree: Boolean,
    private val includeFilters: List<TypeFilter>,
): Iterator<SpringClasspathScannerElement> {
    val metadataReaderFactory = CachingMetadataReaderFactory(resolver)
    val innerIterator = if (classExtensionTree) {
        TODO()
        // ExtendClassTree(classLoader).apply { appendAll(scanned) }.iterator()
    } else scanned.iterator().asSequence()
        .map { Pair(it, 0) }
        .iterator()

    var nextElement: SpringClasspathScannerElement? = null

    init {
        calcNext()
    }

    /**
     * Calc the next element and contain it into [nextElement]
     */
    private fun calcNext() {
        while (innerIterator.hasNext()) {
            val (nextResource, depth) = innerIterator.next()
            val metadataReader = metadataReaderFactory.getMetadataReader(nextResource)
            val classMetadata = metadataReader.classMetadata

            if (includeFilters.all { it.match(metadataReader, metadataReaderFactory) }) {
                nextElement = SpringClasspathScannerElement(
                    ClassFileMetadata(nextResource, metadataReader, classMetadata),
                    depth = depth,
                )
                return
            }
        }

        nextElement = null
    }

    override fun next(): SpringClasspathScannerElement {
        val next = nextElement ?: throw NoSuchElementException()
        calcNext()
        return next
    }

    override fun hasNext(): Boolean {
        return nextElement != null
    }
}

class SpringClasspathScannerElement(
    override val element: ClassFileMetadata,
    override val depth: Int,
) : ScannedElement<ClassFileMetadata> {
    override val className: String
        get() = element.classMetadata.className

    override val classSource: URL?
        get() = element.resource.url

    override val isAbstract: Boolean
        get() = element.classMetadata.isAbstract

    override fun toString(): String {
        return "${this.javaClass.simpleName}(${className}, depth = ${depth})"
    }
}
