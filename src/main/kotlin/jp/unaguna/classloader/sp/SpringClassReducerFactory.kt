package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClassCounter
import jp.unaguna.classloader.core.ClassMaximumReducer
import jp.unaguna.classloader.core.ClassReducer
import jp.unaguna.classloader.core.ClassReducerFactory
import jp.unaguna.classloader.core.ClassReducerType
import jp.unaguna.classloader.core.JavaVersion

sealed class SpringClassReducerType<R> : ClassReducerType<R> {
    object CountAll : SpringClassReducerType<Int>() {
        override fun createFactory(): SpringClassReducerFactory<Int> {
            return SpringClassAllCounterFactory()
        }
    }
    object CountConcrete : SpringClassReducerType<Int>() {
        override fun createFactory(): SpringClassReducerFactory<Int> {
            return SpringConcreteClassCounterFactory()
        }
    }
    object MaxJavaVersion : SpringClassReducerType<JavaVersion>() {
        override fun createFactory(): SpringClassReducerFactory<JavaVersion> {
            return SpringMaxJavaVersionClassReducerFactory()
        }
    }

    abstract fun createFactory(): SpringClassReducerFactory<R>
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

class SpringMaxJavaVersionClassReducerFactory : SpringClassReducerFactory<JavaVersion>() {
    override val type = SpringClassReducerType.MaxJavaVersion

    override fun create(): ClassReducer<ClassFileMetadata, SpringClasspathScannerElement, JavaVersion> {
        return SpringMaxJavaVersionClassReducer()
    }

    private class SpringMaxJavaVersionClassReducer :
        ClassMaximumReducer<ClassFileMetadata, SpringClasspathScannerElement, JavaVersion>() {
        override val type = SpringClassReducerType.MaxJavaVersion
        override fun targetValueOf(element: SpringClasspathScannerElement): JavaVersion {
            return element.javaVersion
        }
    }
}
