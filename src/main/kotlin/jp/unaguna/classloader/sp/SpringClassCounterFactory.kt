package jp.unaguna.classloader.sp

import jp.unaguna.classloader.core.ClassCounter
import jp.unaguna.classloader.core.ClassCounterFactory

sealed class SpringClassCounterFactory : ClassCounterFactory<ClassFileMetadata, SpringClasspathScannerElement>

class SpringClassAllCounterFactory : SpringClassCounterFactory() {
    override fun create(): ClassCounter<ClassFileMetadata, SpringClasspathScannerElement> {
        return SpringClassAllCounter()
    }

    private class SpringClassAllCounter : ClassCounter<ClassFileMetadata, SpringClasspathScannerElement>() {
        override fun matches(element: SpringClasspathScannerElement): Boolean {
            return true
        }
    }
}
