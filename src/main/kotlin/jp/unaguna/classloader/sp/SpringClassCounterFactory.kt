package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClassCounter
import jp.unaguna.classloader.core.ClassReducerFactory

enum class SpringClassCounterType {
    ALL,
    CONCRETE,
}

sealed class SpringClassReducerFactory<R> :
    ClassReducerFactory<ClassFileMetadata, SpringClasspathScannerElement, SpringClassCounterType, R>

class SpringClassAllCounterFactory : SpringClassReducerFactory<Int>() {
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

class SpringConcreteClassCounterFactory : SpringClassReducerFactory<Int>() {
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
