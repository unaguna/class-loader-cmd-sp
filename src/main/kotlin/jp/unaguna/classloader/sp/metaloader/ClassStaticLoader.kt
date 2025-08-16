package jp.unaguna.classloader.sp.metaloader

import jp.unaguna.classloader.core.Visibility
import org.springframework.asm.ClassReader
import org.springframework.asm.Opcodes
import org.springframework.core.io.Resource

class ClassStaticLoader {
    @Suppress("MagicNumber")
    fun load(resource: Resource): StaticClassData {
        resource.inputStream.use { inStream ->
            val cr = ClassReader(inStream)

            val minor = cr.readUnsignedShort(4)
            val major = cr.readUnsignedShort(6)

            return StaticClassData(
                visibility = when {
                    (cr.access and Opcodes.ACC_PUBLIC) != 0 -> Visibility.PUBLIC
                    (cr.access and Opcodes.ACC_PRIVATE) != 0 -> Visibility.PRIVATE
                    (cr.access and Opcodes.ACC_PROTECTED) != 0 -> Visibility.PROTECTED
                    else -> Visibility.PACKAGE_PRIVATE
                },
                major = major,
                minor = minor,
            )
        }
    }
}
