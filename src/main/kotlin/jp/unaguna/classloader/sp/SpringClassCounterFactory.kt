package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClassCounter
import jp.unaguna.classloader.core.ClassCounterFactory

enum class SpringClassCounterType {
    ALL,
}

sealed class SpringClassCounterFactory :
    ClassCounterFactory<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType>

class SpringClassAllCounterFactory : SpringClassCounterFactory() {
    override val type = SpringClassCounterType.ALL

    override fun create(): ClassCounter<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType> {
        return SpringClassAllCounter()
    }

    private class SpringClassAllCounter :
        ClassCounter<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType>() {
        override val type = SpringClassCounterType.ALL
        override fun matches(element: SpringClasspathScannerElement): Boolean {
            return true
        }
    }
}
