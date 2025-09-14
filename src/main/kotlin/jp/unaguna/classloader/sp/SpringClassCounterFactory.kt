package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClassCounter
import jp.unaguna.classloader.core.ClassCounterFactory

enum class SpringClassCounterType {
    ALL,
    CONCRETE,
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

class SpringConcreteClassCounterFactory : SpringClassCounterFactory() {
    override val type = SpringClassCounterType.CONCRETE

    override fun create(): ClassCounter<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType> {
        return SpringConcreteClassCounter()
    }

    private class SpringConcreteClassCounter :
        ClassCounter<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType>() {

        override val type = SpringClassCounterType.CONCRETE
        override fun matches(element: SpringClasspathScannerElement): Boolean {
            return element.isConcrete
        }
    }
}
