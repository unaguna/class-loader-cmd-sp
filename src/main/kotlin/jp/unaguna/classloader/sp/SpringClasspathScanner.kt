package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClasspathScannerResettable
import jp.unaguna.classloader.core.ScannedElement
import jp.unaguna.classloader.core.Visibility
import jp.unaguna.classloader.sp.metaloader.ClassStaticLoader
import jp.unaguna.classloader.sp.tree.ExtendClassTree
import jp.unaguna.classloader.sp.typefilter.NamePatternTypeFilter
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.type.ClassMetadata
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.core.type.filter.TypeFilter
import java.net.URL

class SpringClasspathScanner(
    classLoader: ClassLoader,
) : ClasspathScannerResettable<ClassFileMetadata, SpringClasspathScannerElement> {
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

    override fun subtypeOf(cls: Class<*>) {
        this.addIncludeFilter(AssignableTypeFilter(cls))
    }

    override fun annotatedBy(cls: Class<out Annotation>) {
        this.addIncludeFilter(AnnotationTypeFilter(cls, true))
    }

    override fun pattern(classNamePatterns: Iterable<String>) {
        this.addIncludeFilter(NamePatternTypeFilter(classNamePatterns))
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
) : Iterator<SpringClasspathScannerElement> {
    val metadataReaderFactory = CachingMetadataReaderFactory(resolver)
    val innerIterator = if (classExtensionTree) {
        ExtendClassTree().apply { appendAll(scanned.map { loadMetadata(it) }) }.iterator()
    } else {
        scanned.iterator().asSequence()
            .map { Pair(loadMetadata(it), 0) }
            .iterator()
    }

    var nextElement: SpringClasspathScannerElement? = null

    init {
        calcNext()
    }

    private fun loadMetadata(resource: Resource): ClassFileMetadata {
        val metadataReader = metadataReaderFactory.getMetadataReader(resource)
        val classMetadata = metadataReader.classMetadata

        return ClassFileMetadata(resource, metadataReader, classMetadata)
    }

    /**
     * Calc the next element and contain it into [nextElement]
     */
    private fun calcNext() {
        while (innerIterator.hasNext()) {
            val (nextFileMetadata, depth) = innerIterator.next()

            if (includeFilters.all { it.match(nextFileMetadata.metadataReader, metadataReaderFactory) }) {
                nextElement = SpringClasspathScannerElement(
                    nextFileMetadata,
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

    override val shortClassName: String by lazy {
        val dot = className.lastIndexOf('.')
        return@lazy if (dot < 0) {
            className
        } else {
            className.substring(dot + 1)
        }
    }

    override val classSource: URL?
        get() = element.resource.url

    override val isAbstract: Boolean
        get() = element.classMetadata.isAbstract

    override val isInterface: Boolean
        get() = element.classMetadata.isInterface

    override val isFinal: Boolean
        get() = element.classMetadata.isFinal

    override val isAnnotation: Boolean
        get() = element.classMetadata.isAnnotation

    override val visibility: Visibility
        get() = staticClassData.visibility

    override val major: Int
        get() = staticClassData.major

    override val minor: Int
        get() = staticClassData.minor

    override val serialVersionUID: Long?
        get() = staticClassData.serialVersionUID

    private val staticClassData by lazy {
        ClassStaticLoader().load(element.resource)
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}($className, depth = $depth)"
    }
}
