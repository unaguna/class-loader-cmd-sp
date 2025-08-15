package jp.unaguna.classloader.sp.metaloader

import jp.unaguna.classloader.core.Visibility
import org.springframework.asm.ClassReader
import org.springframework.asm.Opcodes
import org.springframework.core.io.Resource

class ClassStaticLoader {
    fun load(resource: Resource): StaticClassData {
        val access = resource.inputStream.use { inStream ->
            val cr = ClassReader(inStream)
            cr.access
        }
        return StaticClassData(
            visibility = when {
                (access and Opcodes.ACC_PUBLIC) != 0 -> Visibility.PUBLIC
                (access and Opcodes.ACC_PRIVATE) != 0 -> Visibility.PRIVATE
                (access and Opcodes.ACC_PROTECTED) != 0 -> Visibility.PROTECTED
                else -> Visibility.PACKAGE_PRIVATE
            },
        )
    }
}
