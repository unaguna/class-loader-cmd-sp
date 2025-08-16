package jp.unaguna.classloader.sp

import jp.unaguna.classloader.ClassNameGrobMatcher
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.TypeFilter

class NamePatternTypeFilter(private val pattern: String): TypeFilter {
    private val matcher = ClassNameGrobMatcher(ignoreCase = false)

    override fun match(
        metadataReader: MetadataReader,
        metadataReaderFactory: MetadataReaderFactory,
    ): Boolean {
        return matcher.match(pattern, metadataReader.classMetadata.className)
    }
}
