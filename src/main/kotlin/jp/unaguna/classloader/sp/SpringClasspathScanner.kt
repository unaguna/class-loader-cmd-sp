package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClasspathScannerResettable
import jp.unaguna.classloader.core.ScannedElement
import jp.unaguna.classloader.sp.tree.ExtendClassTree
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.type.ClassMetadata
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.filter.AssignableTypeFilter
import java.net.URL

class SpringClasspathScanner(
    private val classLoader: ClassLoader,
): ClasspathScannerResettable<ClassFileMetadata, SpringClasspathScannerElement> {
    private var basePackage: String? = null
    private var classExtensionTree: Boolean = false

    private val resolver = PathMatchingResourcePatternResolver(classLoader)

    override fun scan(): Iterator<SpringClasspathScannerElement> {
        val packageSearchPath = "classpath*:" + (basePackage ?: "").replace('.', '/') + "/**/*.class"
        return SpringClasspathScannerIterator(
            resolver.getResources(packageSearchPath).toList(),
            classExtensionTree = this.classExtensionTree,
            classLoader = this.classLoader,
            resolver = this.resolver,
        )
    }

    override fun subclassOf(cls: Class<*>) {
        TODO()
//        scanner.addIncludeFilter(AssignableTypeFilter(cls))
    }

    override fun inPackage(basePackage: String) {
        this.basePackage = basePackage
    }

    override fun asClassExtensionTree() {
        this.classExtensionTree = true
    }

    override fun clearConditions() {
        TODO()
        // scanner.resetFilters(false)
        // basePackage = null
        // classExtensionTree = false
    }
}

data class ClassFileMetadata(
    val resource: Resource,
    val classMetadata: ClassMetadata,
)

private class SpringClasspathScannerIterator(
    scanned: Iterable<Resource>,
    resolver: ResourcePatternResolver,
    classExtensionTree: Boolean,
    classLoader: ClassLoader,
): Iterator<SpringClasspathScannerElement> {
    val metadataReaderFactory = CachingMetadataReaderFactory(resolver)
    val innerIterator = if (classExtensionTree) {
        TODO()
        // ExtendClassTree(classLoader).apply { appendAll(scanned) }.iterator()
    } else scanned.iterator().asSequence()
        .map { Pair(it, 0) }
        .iterator()

    override fun next(): SpringClasspathScannerElement {
        val (nextResource, depth) = innerIterator.next()
        val metadataReader = metadataReaderFactory.getMetadataReader(nextResource)
        val metadata = metadataReader.classMetadata
        return SpringClasspathScannerElement(
            ClassFileMetadata(nextResource, metadata),
            depth = depth,
        )
    }

    override fun hasNext(): Boolean {
        return innerIterator.hasNext()
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

private class CustomClassPathScanningCandidateComponentProvider(
    classLoader: ClassLoader,
): ClassPathScanningCandidateComponentProvider(false) {
    init {
        resourceLoader = DefaultResourceLoader(classLoader)
    }

    override fun isCandidateComponent(beanDefinition: AnnotatedBeanDefinition): Boolean {
        // 元の実装だと抽象クラスやインターフェース (つまり Bean にできないもの) が対象外になってしまうので改造
        return true
    }
}
