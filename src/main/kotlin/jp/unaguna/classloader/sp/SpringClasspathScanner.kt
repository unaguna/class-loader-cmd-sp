package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClasspathScannerResettable
import jp.unaguna.classloader.core.ScannedElement
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.type.filter.AssignableTypeFilter

class SpringClasspathScanner(
    private val classLoader: ClassLoader,
): ClasspathScannerResettable<BeanDefinition, SpringClasspathScannerElement> {
    private var basePackage: String? = null

    private val scanner = ClassPathScanningCandidateComponentProvider(false).apply {
        resourceLoader = DefaultResourceLoader(classLoader)
    }

    override fun scan(): Iterator<SpringClasspathScannerElement> {
        return SpringClasspathScannerIterator(scanner.findCandidateComponents(basePackage ?: ""))
    }

    override fun subclassOf(cls: Class<*>) {
        scanner.addIncludeFilter(AssignableTypeFilter(cls))
    }

    override fun inPackage(basePackage: String) {
        this.basePackage = basePackage
    }

    override fun clearConditions() {
        scanner.resetFilters(false)
        basePackage = null
    }
}

private class SpringClasspathScannerIterator(scanned: Iterable<BeanDefinition>): Iterator<SpringClasspathScannerElement> {
    val innerIterator = scanned.iterator()

    override fun next(): SpringClasspathScannerElement {
        val innerNext = innerIterator.next()
        return SpringClasspathScannerElement(innerNext)
    }

    override fun hasNext(): Boolean {
        return innerIterator.hasNext()
    }
}

class SpringClasspathScannerElement(override val element: BeanDefinition) : ScannedElement<BeanDefinition> {

}
