package jp.unaguna.classloader.sp

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.type.filter.AssignableTypeFilter

class SubclassDefinitionProvider(
    private val classLoader: ClassLoader,
    private val superClass: Class<*>,
    private val basePackage: String = "",
) : BeanDefinitionProvider() {
    private val scanner = ClassPathScanningCandidateComponentProvider(false).apply {
        resourceLoader = DefaultResourceLoader(classLoader)
        addIncludeFilter(AssignableTypeFilter(superClass))
    }

    override fun iterator(): Iterator<BeanDefinition> {
        return scanner.findCandidateComponents(basePackage).iterator()
    }
}
