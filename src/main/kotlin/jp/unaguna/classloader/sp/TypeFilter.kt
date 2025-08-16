package jp.unaguna.classloader.sp

import jp.unaguna.classloader.ClassNameGrobMatcher
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.TypeFilter

class NamePatternTypeFilter(patterns: Iterable<String>) : TypeFilter {
    private val patterns = patterns.toList()
    private val matcher = ClassNameGrobMatcher(ignoreCase = false)

    override fun match(
        metadataReader: MetadataReader,
        metadataReaderFactory: MetadataReaderFactory,
    ): Boolean {
        val className = metadataReader.classMetadata.className
        return patterns.any { matcher.match(it, className) }
    }
}
