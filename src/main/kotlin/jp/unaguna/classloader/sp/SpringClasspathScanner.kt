package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClasspathScannerResettable
import jp.unaguna.classloader.core.ScannedElement
import jp.unaguna.classloader.sp.tree.ExtendClassTree
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ScannedGenericBeanDefinition
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.type.filter.AssignableTypeFilter
import java.net.URL

class SpringClasspathScanner(
    private val classLoader: ClassLoader,
): ClasspathScannerResettable<BeanDefinition, SpringClasspathScannerElement> {
    private var basePackage: String? = null
    private var classExtensionTree: Boolean = false

    private val scanner = ClassPathScanningCandidateComponentProvider(false).apply {
        resourceLoader = DefaultResourceLoader(classLoader)
    }

    override fun scan(): Iterator<SpringClasspathScannerElement> {
        return SpringClasspathScannerIterator(
            scanner.findCandidateComponents(basePackage ?: ""),
            classExtensionTree = this.classExtensionTree,
            classLoader = this.classLoader,
        )
    }

    override fun subclassOf(cls: Class<*>) {
        scanner.addIncludeFilter(AssignableTypeFilter(cls))
    }

    override fun inPackage(basePackage: String) {
        this.basePackage = basePackage
    }

    override fun asClassExtensionTree() {
        this.classExtensionTree = true
    }

    override fun clearConditions() {
        scanner.resetFilters(false)
        basePackage = null
        classExtensionTree = false
    }
}

private class SpringClasspathScannerIterator(
    scanned: Iterable<BeanDefinition>,
    classExtensionTree: Boolean,
    classLoader: ClassLoader,
): Iterator<SpringClasspathScannerElement> {
    val innerIterator = if (classExtensionTree) {
        ExtendClassTree(classLoader).apply { appendAll(scanned) }.iterator()
    } else scanned.iterator().asSequence().map { Pair(it, 0) }.iterator()

    override fun next(): SpringClasspathScannerElement {
        val (innerNext, depth) = innerIterator.next()
        return SpringClasspathScannerElement(
            innerNext,
            depth = depth,
        )
    }

    override fun hasNext(): Boolean {
        return innerIterator.hasNext()
    }
}

class SpringClasspathScannerElement(
    override val element: BeanDefinition,
    override val depth: Int,
) : ScannedElement<BeanDefinition> {
    private val bd = element as ScannedGenericBeanDefinition

    override val className: String
        get() = element.beanClassName!!

    override val classSource: URL?
        get() = when (val src = element.source) {
            is Resource -> src.url
            else -> null
        }

    override val isAbstract: Boolean
        get() = bd.metadata.isAbstract
}
