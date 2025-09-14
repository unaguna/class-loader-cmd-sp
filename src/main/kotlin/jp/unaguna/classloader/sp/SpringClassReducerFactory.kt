package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClassCounter
import jp.unaguna.classloader.core.ClassReducerFactory
import jp.unaguna.classloader.core.ClassReducerType

sealed class SpringClassReducerType<R> : ClassReducerType<R> {
    object CountAll : SpringClassReducerType<Int>()
    object CountConcrete : SpringClassReducerType<Int>()
}

sealed class SpringClassReducerFactory<R> :
    ClassReducerFactory<ClassFileMetadata, SpringClasspathScannerElement, R>

class SpringClassAllCounterFactory : SpringClassReducerFactory<Int>() {
    override val type = SpringClassReducerType.CountAll

    override fun create(): ClassCounter<ClassFileMetadata, SpringClasspathScannerElement> {
        return SpringClassAllCounter()
    }

    private class SpringClassAllCounter : ClassCounter<ClassFileMetadata, SpringClasspathScannerElement>() {
        override val type = SpringClassReducerType.CountAll
        override fun matches(element: SpringClasspathScannerElement): Boolean {
            return true
        }
    }
}

class SpringConcreteClassCounterFactory : SpringClassReducerFactory<Int>() {
    override val type = SpringClassReducerType.CountConcrete

    override fun create(): ClassCounter<ClassFileMetadata, SpringClasspathScannerElement> {
        return SpringConcreteClassCounter()
    }

    private class SpringConcreteClassCounter : ClassCounter<ClassFileMetadata, SpringClasspathScannerElement>() {
        override val type = SpringClassReducerType.CountConcrete
        override fun matches(element: SpringClasspathScannerElement): Boolean {
            return element.isConcrete
        }
    }
}
